package supercoder79.structurefixer;

import com.mojang.datafixers.DataFixer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
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

        update(file, "", outPath, fixer);
    }

    private static void update(File directory, String path, Path output, DataFixer fixer) {
        for (File file : directory.listFiles()) {
            String name = file.getName();

            if (file.isDirectory()) {
                update(file, path + name + "/", output, fixer);
                continue;
            }

            String absolutePath = file.getAbsolutePath();
            System.out.println("Trying to update " + absolutePath);

            try {
                CompoundTag tag = NbtIo.readCompressed(file);

                if (!tag.contains("DataVersion", 99)) {
                    tag.putInt("DataVersion", 500);
                }

                CompoundTag fixedTag = NbtUtils.update(fixer, DataFixTypes.STRUCTURE, tag, tag.getInt("DataVersion"));
                StructureTemplate structureTemplate = new StructureTemplate();
                structureTemplate.load(BuiltInRegistries.BLOCK.asLookup(), fixedTag);

                Path outDir = output.resolve(path);
                outDir.toFile().mkdirs();
                CompoundTag resultTag = structureTemplate.save(new CompoundTag());

                NbtIo.writeCompressed(resultTag, outDir.resolve(name).toFile());

                System.out.println("Updated " + absolutePath + " from version " + tag.getInt("DataVersion") + " to " + resultTag.getInt("DataVersion"));
            } catch (Exception e) {
                System.out.println("Could not update " + absolutePath);
            }
        }
    }
}
