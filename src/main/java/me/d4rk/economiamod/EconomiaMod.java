package me.d4rk.economiamod;

import me.d4rk.economiamod.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = EconomiaMod.MODID, name = EconomiaMod.NAME, version = EconomiaMod.VERSION, dependencies = "required-after:enderpay", useMetadata = true)
public class EconomiaMod {

    public static final String MODID = "economiamod";
    public static final String NAME = "Economia";
    public static final String VERSION = "1.0";

    @SidedProxy(clientSide = "me.d4rk.economiamod.proxy.ClientProxy", serverSide = "me.d4rk.economiamod.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static EconomiaMod instance;

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

}
