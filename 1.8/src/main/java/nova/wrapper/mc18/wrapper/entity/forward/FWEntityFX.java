package nova.wrapper.mc18.wrapper.entity.forward;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nova.core.block.Stateful;
import nova.core.component.Updater;
import nova.core.component.misc.Collider;
import nova.core.component.transform.EntityTransform;
import nova.core.entity.Entity;
import nova.core.entity.EntityFactory;
import nova.core.util.shape.Cuboid;
import nova.wrapper.mc18.render.RenderUtility;

/**
 * A copy of BWEntity that extends EntityFX
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
public class FWEntityFX extends EntityFX {

	public final Entity wrapped;
	public final EntityTransform transform;

	boolean firstTick = true;

	public FWEntityFX(World world, EntityFactory factory) {
		super(world, 0, 0, 0);
		this.wrapped = factory.make();
		this.transform = new MCEntityTransform(this);
		wrapped.add(transform);
		entityInit();
	}

	public FWEntityFX(World world, Entity entity) {
		super(world, 0, 0, 0);
		this.wrapped = entity;
		this.transform = new MCEntityTransform(this);
		wrapped.add(transform);
		entityInit();
	}

	@Override
	public void renderParticle(WorldRenderer worldRendererIn, net.minecraft.entity.Entity p_180434_2_, float p_70539_2_, float x, float y, float z, float p_70539_6_, float p_70539_7_) {
		if (firstTick) {
			prevPosX = posX;
			prevPosY = posY;
			prevPosZ = posZ;
			prevRotationYaw = rotationYaw;
			prevRotationPitch = rotationPitch;
			setPosition(posX, posY, posZ);
			firstTick = false;
		}
		float f11 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) p_70539_2_ - interpPosX);
		float f12 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) p_70539_2_ - interpPosY);
		float f13 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) p_70539_2_ - interpPosZ);

		Tessellator.getInstance().draw();
		FWEntityRenderer.render(this, wrapped, f11, f12, f13);
		Tessellator.getInstance().getWorldRenderer().startDrawingQuads();
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(RenderUtility.particleResource);
	}

	/**
	 * All methods below here are exactly the same between FWEntity and FWEntityFX.
	 * *****************************************************************************
	 */
	@Override
	protected void entityInit() {
		//MC calls entityInit() before we finish wrapping, so this variable is required to check if wrapped exists.
		if (wrapped != null) {
			wrapped.events.publish(new Stateful.LoadEvent());
			prevPosX = posX;
			prevPosY = posY;
			prevPosZ = posZ;
			prevRotationYaw = rotationYaw;
			prevRotationPitch = rotationPitch;
			setPosition(posX, posY, posZ);
		}
	}

	@Override
	public void onUpdate() {
		//TODO: Minecraft's collision is messed up (gets concurrent problems)
		this.noClip = true;
		this.particleAge = 0;
		super.onUpdate();
		double deltaTime = 0.05;

		if (wrapped instanceof Updater) {
			((Updater) wrapped).update(deltaTime);
		}

		//Wrap entity collider
		if (wrapped.has(Collider.class)) {
			Collider collider = wrapped.get(Collider.class);

			//Transform cuboid based on entity.
			Cuboid size = collider
				.boundingBox
				.get();
			//	.scalarMultiply(transform.scale());

			//Sadly Minecraft doesn't support rotated cuboids. And fixed x-z sizes. We take average..
			float width = (float) ((size.max.getX() - size.min.getX()) + (size.max.getZ() - size.min.getZ())) / 2;
			float height = (float) (size.max.getY() - size.min.getY());
			setSize(width, height);
		}

		/**
		 * Update all components in the entity.
		 */
		wrapped.components()
			.stream()
			.filter(component -> component instanceof Updater)
			.forEach(component -> ((Updater) component).update(deltaTime));
	}

	@Override
	public void setDead() {
		wrapped.events.publish(new Stateful.UnloadEvent());
		super.setDead();
	}
}
