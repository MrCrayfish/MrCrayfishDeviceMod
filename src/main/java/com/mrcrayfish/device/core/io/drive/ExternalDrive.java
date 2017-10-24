package com.mrcrayfish.device.core.io.drive;

import com.mrcrayfish.device.core.io.ServerFolder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Author: MrCrayfish
 */
public final class ExternalDrive extends AbstractDrive
{
    private static final Predicate<NBTTagCompound> PREDICATE_DRIVE_TAG = tag ->
            tag.hasKey("name", Constants.NBT.TAG_STRING)
            && tag.hasKey("uuid", Constants.NBT.TAG_STRING)
            && tag.hasKey("root", Constants.NBT.TAG_COMPOUND);

    private ExternalDrive() {}

    public ExternalDrive(String displayName)
    {
        super(displayName);
    }

    @Nullable
    public static AbstractDrive fromTag(NBTTagCompound driveTag)
    {
        if(!PREDICATE_DRIVE_TAG.test(driveTag))
            return null;

        AbstractDrive drive = new ExternalDrive();
        drive.name = driveTag.getString("name");
        drive.uuid = UUID.fromString(driveTag.getString("uuid"));

        NBTTagCompound folderTag = driveTag.getCompoundTag("root");
        drive.root = ServerFolder.fromTag(folderTag.getString("file_name"), folderTag.getCompoundTag("data"));

        return drive;
    }

    @Override
    public NBTTagCompound toTag()
    {
        NBTTagCompound driveTag = new NBTTagCompound();
        driveTag.setString("name", name);
        driveTag.setString("uuid", uuid.toString());

        NBTTagCompound folderTag = new NBTTagCompound();
        folderTag.setString("file_name", root.getName());
        folderTag.setTag("data", root.toTag());
        driveTag.setTag("root", folderTag);

        return driveTag;
    }

    @Override
    public Type getType()
    {
        return Type.EXTERNAL;
    }
}
