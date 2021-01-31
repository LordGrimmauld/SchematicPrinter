package mod.grimmauld.schematicprinter;

import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SchematicPrinter.MODID)
@SuppressWarnings("unused")
public class SchematicPrinter {
	public static final String MODID = "schematicprinter";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final String NAME = "Schematic Printer";

	public SchematicPrinter() {
		MinecraftForge.EVENT_BUS.register(new EventListener());
		IEventBus modEventBus = FMLJavaModLoadingContext.get()
			.getModEventBus();
		modEventBus.addListener(this::clientInit);
		modEventBus.addListener(this::init);
	}

	private void clientInit(FMLClientSetupEvent event) {
		DistExecutor.runWhenOn(Dist.CLIENT, () -> SchematicPrinterClient::init);
	}

	private void init(final FMLCommonSetupEvent event) {
	}
}
