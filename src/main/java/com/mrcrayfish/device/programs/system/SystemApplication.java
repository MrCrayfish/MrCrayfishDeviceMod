package com.mrcrayfish.device.programs.system;

import javax.annotation.Nullable;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.core.Laptop;

/**
 * Created by Casey on 03-Aug-17.
 */
public abstract class SystemApplication extends Application {
	private Laptop laptop;

	SystemApplication() {
	}

	public void setLaptop(@Nullable Laptop laptop) {
		this.laptop = laptop;
	}

	@Nullable
	public Laptop getLaptop() {
		return laptop;
	}
}
