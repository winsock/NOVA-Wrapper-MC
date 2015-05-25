package nova.wrapper.mc1710.forward.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import nova.core.component.Updater;
import nova.core.component.renderer.DynamicRenderer;
import nova.core.component.transform.EntityTransform;
import nova.core.entity.Entity;
import nova.core.entity.EntityFactory;
import nova.core.entity.EntityWrapper;
import nova.core.util.transform.matrix.MatrixStack;
import nova.wrapper.mc1710.backward.render.BWModel;

import java.util.Optional;

/**
 * A copy of BWEntity that extends EntityFX
 * @author Calclavia
 */
@SideOnly(Side.CLIENT)
public class FWEntityFX extends EntityFX implements EntityWrapper {

	public final Entity wrapped;
	public final EntityTransform transform;

	public FWEntityFX(World world, EntityFactory factory) {
		super(world, 0, 0, 0);
		this.wrapped = factory.makeEntity(this);
		this.transform = wrapped.transform;
	}

	public FWEntityFX(World world, Entity entity) {
		super(world, 0, 0, 0);
		this.wrapped = entity;
		this.transform = wrapped.transform;
	}

	@Override
	public void renderParticle(Tessellator tess, float x, float y, float z, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
		Optional<DynamicRenderer> opRenderer = wrapped.get(DynamicRenderer.class);
		if (opRenderer.isPresent()) {
			BWModel model = new BWModel();
			model.matrix = new MatrixStack().translate(x, y, z).rotate(transform.rotation()).getMatrix();
			opRenderer.get().renderDynamic(model);
			model.renderWorld(worldObj);
		}
	}

	/**
	 * All methods below here are exactly the same between FWEntity and FWEntityFX.
	 * *****************************************************************************
	 */
	@Override
	protected void entityInit() {
		//MC calls entityInit() before we finish wrapping, so this variable is required to check if wrapped exists.
		if (wrapped != null) {
			wrapped.awake();
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		double deltaTime = 0.05;

		if (wrapped instanceof Updater) {
			((Updater) wrapped).update(deltaTime);
		}

		/**
		 * Update all components in the entity.
		 */
		wrapped.components()
			.stream()
			.filter(component -> component instanceof Updater)
			.forEach(component -> ((Updater) component).update(deltaTime));
	}
}