package AAADEVRECORDV3.util;



import com.avaya.asm.datamgr.AssetDM;
import com.avaya.asm.datamgr.DMFactory;
import com.avaya.collaboration.util.logger.Logger;

public final class TrafficInterfaceAddressRetrieverImpl implements TrafficInterfaceAddressRetriever
{
    private final AssetDM assetDM;

    private final Logger logger;

    public TrafficInterfaceAddressRetrieverImpl()
    {
        this((AssetDM) DMFactory.getInstance().getDataMgr(AssetDM.class),
                Logger.getLogger(TrafficInterfaceAddressRetriever.class));
    }

    TrafficInterfaceAddressRetrieverImpl(final AssetDM assetDM, final Logger logger)
    {
        this.assetDM = assetDM;

        this.logger = logger;
    }

    @Override
    public String getTrafficInterfaceAddress()
    {
        final String localAsset = assetDM.getMyAssetIp();

        if (logger.isFinestEnabled())
        {
            
        }

        return localAsset;
    }
}