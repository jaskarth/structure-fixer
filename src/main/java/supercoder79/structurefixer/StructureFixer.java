package supercoder79.structurefixer;

import com.mojang.datafixers.DataFixer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.io.File;
import java.nio.file.Path;

public class StructureFixer implements ModInitializer {
    @Override
    public void onInitialize() {
        DataFixer fixer = Minecraft.getInstance().getFixerUpper();

        File file = Path.of(".", "structures").toFile();
        Path outPath = Path.of(".", "structures_new");
        File output = outPath.toFile();
        file.mkdirs();
        output.mkdirs();

        updateAllInDirectory(file, "", outPath, fixer);
    }

    private static void updateAllInDirectory(File directory, String path, Path output, DataFixer fixer) {
        for (File file : directory.listFiles()) {
            String name = file.getName();

            // Recurse directories
            if (file.isDirectory()) {
                updateAllInDirectory(file, path + name + "/", output, fixer);
                continue;
            }

            Path outDir = output.resolve(path);
            updateFile(file, outDir, fixer);
        }
    }

    private static void updateFile(File file, Path outDir, DataFixer fixer) {
        String absolutePath = file.getAbsolutePath();
        String name = file.getName();

        System.out.println("Trying to update " + absolutePath);

        if (file.isDirectory()) {
            System.out.println("Skipping " + absolutePath + " because it is a directory");
            return;
        }

        try {
            CompoundTag tag = NbtIo.readCompressed(file);

            if (!tag.contains("DataVersion", 99)) {
                tag.putInt("DataVersion", 500);
            }

            int currentDataVersion = tag.getInt("DataVersion");
            int requiredMinimumDataVersion = SharedConstants.getCurrentVersion().getDataVersion().getVersion();
            if (currentDataVersion < requiredMinimumDataVersion) {
                CompoundTag fixedTag = DataFixTypes.STRUCTURE.update(fixer, tag, currentDataVersion, SharedConstants.getCurrentVersion().getDataVersion().getVersion());

                // Create a StructureTemplate from the fixed tag and save it to NBT
                StructureTemplate structureTemplate = new StructureTemplate();
                structureTemplate.load(BuiltInRegistries.BLOCK.asLookup(), fixedTag);
                CompoundTag resultTag = structureTemplate.save(new CompoundTag());

                // Write the new NBT to file
                outDir.toFile().mkdirs();
                NbtIo.writeCompressed(resultTag, outDir.resolve(name).toFile());

                System.out.println("Updated " + absolutePath + " from version " + tag.getInt("DataVersion") + " to " + resultTag.getInt("DataVersion"));
            }
        } catch (Exception e) {
            System.out.println("Could not update " + absolutePath +": " + e.getMessage());
        }
    }
}
