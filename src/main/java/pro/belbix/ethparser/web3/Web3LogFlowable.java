package pro.belbix.ethparser.web3;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.log4j.Log4j2;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.EthLog.LogResult;
import org.web3j.protocol.core.methods.response.Log;
import pro.belbix.ethparser.model.Web3Model;

@Log4j2
public class Web3LogFlowable implements Runnable {

  public static final int BLOCKS_STEP = 1000;
  public static final int WAIT_BETWEEN_BLOCKS = 5 * 1000;
  private final AtomicBoolean run = new AtomicBoolean(true);
  private final Web3Functions web3Functions;
  private final List<String> addresses;
  private final List<BlockingQueue<Web3Model<Log>>> logConsumers;
  private final String network;
  private Integer from;
  private BigInteger lastBlock;

  public Web3LogFlowable(
      EthFilter filter,
      Web3Functions web3Functions,
      List<BlockingQueue<Web3Model<Log>>> logConsumers,
      String network) {
    this.web3Functions = web3Functions;
    this.addresses = filter.getAddress();
    this.from = ((DefaultBlockParameterNumber) filter.getFromBlock()).getBlockNumber().intValue();
    this.logConsumers = logConsumers;
    this.network = network;
  }

  public void stop() {
    run.set(false);
  }

  @SuppressWarnings("BusyWait")
  @Override
  public void run() {
    log.info("Start LogFlowable");
    BigInteger currentBlock;
    while (run.get()) {
      try {
        currentBlock = web3Functions.fetchCurrentBlock(network);
        if (lastBlock != null && lastBlock.intValue() >= currentBlock.intValue()) {
          Thread.sleep(WAIT_BETWEEN_BLOCKS);
          continue;
        }
        lastBlock = currentBlock;
        int to = currentBlock.intValue();
        if (from == null) {
          from = to;
        } else {
          int diff = to - from;
          if (diff > BLOCKS_STEP) {
            to = from + BLOCKS_STEP;
          }
        }
        //noinspection rawtypes
        List<EthLog.LogResult> logResults = web3Functions.fetchContractLogs(addresses, from, to, network);
        log.info("Parse log from {} to {} on block: {} - {}", from, to,
            currentBlock, logResults.size());
        //noinspection rawtypes
        for (LogResult logResult : logResults) {
          Log ethLog = (Log) logResult.get();
          if (ethLog == null) {
            continue;
          }
          for (BlockingQueue<Web3Model<Log>> queue : logConsumers) {
            writeInQueue(queue, ethLog);
          }
        }
        from = to + 1;
      } catch (Exception e) {
        log.error("Error in log flow", e);
      }
    }
  }

  private <T> void writeInQueue(BlockingQueue<Web3Model<T>> queue, T o) {
    try {
      Web3Model<T> model = new Web3Model<>(o, network);
      while (!queue.offer(model, 60, SECONDS)) {
        log.warn("The queue is full for {}", o.getClass().getSimpleName());
      }
    } catch (Exception e) {
      log.error("Error write in queue", e);
    }
  }
}