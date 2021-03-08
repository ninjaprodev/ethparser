package pro.belbix.ethparser.web3.contracts;

import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import pro.belbix.ethparser.Application;
import pro.belbix.ethparser.entity.contracts.PoolEntity;
import pro.belbix.ethparser.entity.contracts.TokenEntity;
import pro.belbix.ethparser.entity.contracts.TokenToUniPairEntity;
import pro.belbix.ethparser.entity.contracts.UniPairEntity;
import pro.belbix.ethparser.entity.contracts.VaultEntity;
import pro.belbix.ethparser.entity.contracts.VaultToPoolEntity;
import pro.belbix.ethparser.properties.AppProperties;
import pro.belbix.ethparser.repositories.eth.PoolRepository;
import pro.belbix.ethparser.repositories.eth.TokenRepository;
import pro.belbix.ethparser.repositories.eth.TokenToUniPairRepository;
import pro.belbix.ethparser.repositories.eth.UniPairRepository;
import pro.belbix.ethparser.repositories.eth.VaultRepository;
import pro.belbix.ethparser.repositories.eth.VaultToPoolRepository;
import pro.belbix.ethparser.web3.Web3Service;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
public class ContractLoaderTest {

    @Autowired
    private ContractLoader contractLoader;
    @Autowired
    private AppProperties appProperties;
    @Autowired
    private VaultRepository vaultRepository;
    @Autowired
    private PoolRepository poolRepository;
    @Autowired
    private UniPairRepository uniPairRepository;
    @Autowired
    private TokenRepository tokenRepository;
  @Autowired
  private VaultToPoolRepository vaultToPoolRepository;
  @Autowired
  private TokenToUniPairRepository tokenToUniPairRepository;
  @Autowired
  private Web3Service web3Service;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Before
  public void setUp() throws Exception {
    contractLoader.load();
  }

  @Test
//    @Ignore
  public void fullRunShouldBeOk() throws JsonProcessingException {
//        appProperties.setUpdateContracts(true);
    System.out.println("**************** VAULTS ************************");
    for (VaultEntity vaultEntity : vaultRepository.findAll()) {
      assertNotNull(vaultEntity);
      System.out.println(objectMapper.writeValueAsString(vaultEntity));
    }
        System.out.println("**************** POOLS ************************");
        for (PoolEntity poolEntity : poolRepository.findAll()) {
            assertNotNull(poolEntity);
            System.out.println(objectMapper.writeValueAsString(poolEntity));
        }
        System.out.println("**************** UNI PAIRS ************************");
        for (UniPairEntity uniPairEntity : uniPairRepository.findAll()) {
            assertNotNull(uniPairEntity);
            System.out.println(objectMapper.writeValueAsString(uniPairEntity));
        }
        System.out.println("**************** TOKENS ************************");
        for (TokenEntity tokenEntity : tokenRepository.findAll()) {
            assertNotNull(tokenEntity);
            System.out.println(objectMapper.writeValueAsString(tokenEntity));
        }
        System.out.println("**************** VAULT TO POOLS ************************");
        for (VaultToPoolEntity vaultToPoolEntity : vaultToPoolRepository.findAll()) {
            assertNotNull(vaultToPoolEntity);
            System.out.println(objectMapper.writeValueAsString(vaultToPoolEntity));
        }
        System.out.println("**************** TOKEN TO UNI ************************");
        for (TokenToUniPairEntity tokenToUniPairEntity : tokenToUniPairRepository.findAll()) {
            assertNotNull(tokenToUniPairEntity);
            System.out.println(objectMapper.writeValueAsString(tokenToUniPairEntity));
        }
    }

    @Test
    @Ignore
    public void loadKeyBlocks() {
        appProperties.setUpdateContracts(true);
        contractLoader.loadKeyBlocks();
    }

    //    @Test
//    public void containsAllVaults() {
//        contractLoader.load();
//        for (String vaultAddress : ContractUtils.getAllVaultAddresses()) {
//            boolean found = false;
//            for (PoolEntity poolEntity : ContractLoader.poolsCacheByAddress.values()) {
//                if (vaultAddress.equals(poolEntity.getLpToken().getAddress())) {
//                    found = true;
//                    break;
//                }
//            }
//            if (!found) {
//                System.out.println("not found " + vaultAddress);
//            }
//        }
//    }
}
