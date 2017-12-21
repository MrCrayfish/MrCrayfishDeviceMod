package com.mrcrayfish.device.programs;

import java.awt.Color;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.component.Button;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Author: MrCrayfish
 */
public class ApplicationTest extends Application {
	@Override
	public void init() {
		Button button = new Button(5, 5, "Print", Icons.PRINTER);
		button.setClickListener((c, mouseButton) -> {
			if (mouseButton == 0) {
				int[] pixels = { Color.RED.getRGB(), Color.DARK_GRAY.getRGB(), Color.YELLOW.getRGB(),
						Color.MAGENTA.getRGB() };
				Dialog.Print dialog = new Dialog.Print(new ApplicationPixelPainter.PicturePrint("Test", pixels, 2));
				openDialog(dialog);
			}
		});
		super.addComponent(button);
	}

	@Override
	public void load(NBTTagCompound tagCompound) {

	}

	@Override
	public void save(NBTTagCompound tagCompound) {

	}
}
