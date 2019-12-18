import com.ctrip.framework.apollo.core.enums.Env;
import com.jimistore.nemo.apollo.enums.MetaAddressEnum;
import com.jimistore.nemo.apollo.spi.NemoMetaServerProvider;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * 自定义metaServer获取逻辑测试用例
 *
 * @author hexin
 */
public class NemoMetaServerProviderTest {

    @Test
    public void testGetMetaServerProvider(){
        Env env = Env.TEST;
        String testEnvMetaAddress = MetaAddressEnum.TEST.getAddress();

        NemoMetaServerProvider nemoMetaServerProvider = new NemoMetaServerProvider();
        assertEquals(testEnvMetaAddress, nemoMetaServerProvider.getMetaServerAddress(env));
    }
}
