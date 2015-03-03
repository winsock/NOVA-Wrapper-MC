package nova.wrapper.mc1710.backward.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import nova.core.entity.Entity;
import nova.core.gui.Gui;
import nova.core.gui.factory.GuiFactory;
import nova.core.util.exception.NovaException;
import nova.core.util.transform.Vector3i;
import nova.wrapper.mc1710.backward.entity.BWEntityPlayer;
import nova.wrapper.mc1710.backward.gui.MCGui.MCContainer;
import nova.wrapper.mc1710.backward.gui.MCGui.MCGuiScreen;
import nova.wrapper.mc1710.launcher.NovaMinecraft;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;

public class MCGuiFactory extends GuiFactory {

	private static Optional<Gui> guiToOpen = Optional.empty();
	private static List<Gui> idMappedGUIs = new ArrayList<>();

	@Override
	public void registerGui(Gui gui, String modID) {
		super.registerGui(gui, modID);
		idMappedGUIs.add(gui);
	}

	@Override
	protected void showGui(Gui gui, Entity entity, Vector3i pos) {
		BWEntityPlayer player = (BWEntityPlayer) entity;
		int id = idMappedGUIs.indexOf(gui);
		guiToOpen = Optional.of(gui);
		if (player.entity.worldObj.isRemote != gui.hasServerSide()) {
			player.entity.openGui(NovaMinecraft.id, id, player.entity.getEntityWorld(), pos.x, pos.y, pos.z);
		}
	}

	@Override
	protected void closeGui(Gui gui) {
		gui.unbind();
		FMLCommonHandler.instance().showGuiScreen(null);
	}

	@Override
	public Optional<Gui> getActiveGuiImpl() {
		GuiScreen gui = Minecraft.getMinecraft().currentScreen;
		if (gui instanceof MCGuiScreen) {
			return Optional.of(((MCGuiScreen) gui).getGui().getComponent());
		}
		return Optional.empty();
	}

	@Override
	public Optional<Gui> getActiveGuiImpl(Entity player) {
		BWEntityPlayer entityPlayer = (BWEntityPlayer) player;
		Container container = entityPlayer.entity.openContainer;
		if (container instanceof MCContainer) {
			return Optional.of(((MCContainer) container).getGui().getComponent());
		}
		return Optional.empty();
	}

	public static class GuiHandler implements IGuiHandler {

		@Override
		public Container getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
			if (guiToOpen.isPresent()) {
				Gui gui = guiToOpen.get();
				guiToOpen = Optional.empty();
				gui.bind(new BWEntityPlayer(player), new Vector3i(x, y, z));
				return ((MCGui) gui.getNative()).newContainer();
			}
			return null;
		}

		@Override
		public GuiContainer getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
			Gui gui;
			if (guiToOpen.isPresent()) {
				// Check if the client side GUI instance was set with showGui on
				// the client
				gui = guiToOpen.get();
				guiToOpen = Optional.empty();
			} else {
				// Try to get the client side GUI from the id mapping
				gui = idMappedGUIs.get(id);
			}
			if (gui == null)
				throw new NovaException("Couldn't get client side instance for the provided GUI of id " + id + " !");

			gui.bind(new BWEntityPlayer(player), new Vector3i(x, y, z));

			MCGui nativeGui = (MCGui) gui.getNative();
			nativeGui.newContainer();
			return nativeGui.getGuiScreen();
		}
	}
}
