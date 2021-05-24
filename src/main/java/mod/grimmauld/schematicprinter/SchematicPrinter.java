package mod.grimmauld.schematicprinter;

import mod.grimmauld.schematicprinter.client.SchematicPrinterClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
	}

	private void clientInit(FMLClientSetupEvent event) {
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> SchematicPrinterClient::init);
	}
}
