package nova.wrapper.mc1710.forward.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import nova.core.entity.Entity;
import nova.core.entity.EntityFactory;
import nova.core.entity.EntityWrapper;
import nova.core.retention.Data;
import nova.core.retention.Storable;
import nova.core.util.components.Updater;
import nova.core.util.transform.Quaternion;
import nova.core.util.transform.vector.Vector3d;
import nova.wrapper.mc1710.backward.world.BWWorld;
import nova.wrapper.mc1710.util.DataUtility;

import java.util.Arrays;

/**
 * Entity wrapper
 * @author Calclavia
 */
public class FWEntity extends net.minecraft.entity.Entity implements EntityWrapper {

	public final Entity wrapped;
	private final MCRigidBody rigidBody = new MCRigidBody(this);

	public FWEntity(World world, EntityFactory factory, Object... args) {
		super(world);
		this.wrapped = factory.makeEntity(this, args);
		entityInit();
	}

	public FWEntity(World world, Entity wrapped) {
		super(world);
		this.wrapped = wrapped;
		entityInit();
	}

	@Override
	protected void entityInit() {
		// Sadly, Minecraft wants to wake us up before we're done wrapping...
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

		rigidBody.update(deltaTime);

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		if (wrapped instanceof Storable) {
			((Storable) wrapped).load(DataUtility.nbtToData(nbt));
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {

		if (wrapped instanceof Storable) {
			Data data = new Data();
			((Storable) wrapped).save(data);
			DataUtility.dataToNBT(nbt, data);
		}
	}

	/**
	 * Entity Wrapper Methods
	 * @return
	 */
	@Override
	public nova.core.world.World world() {
		return new BWWorld(worldObj);
	}

	@Override
	public Vector3d position() {
		return new Vector3d(posX, posY, posZ);
	}

	@Override
	public Quaternion rotation() {
		return Quaternion.fromEuler(Math.toRadians(rotationYaw), Math.toRadians(rotationPitch), 0);
	}

	@Override
	public void setWorld(nova.core.world.World world) {
		travelToDimension(Arrays
				.stream(DimensionManager.getWorlds())
				.filter(w -> w.getProviderName().equals(world.getID()))
				.findAny()
				.get()
				.provider
				.dimensionId
		);
	}

	@Override
	public void setPosition(Vector3d position) {
		setPosition(position.x, position.y, position.z);
	}

	@Override
	public void setRotation(Quaternion rotation) {
		Vector3d euler = rotation.toEuler();
		setRotation((float) Math.toDegrees(euler.x), (float) Math.toDegrees(euler.y));
	}

	@Override
	public double mass() {
		return rigidBody.mass();
	}

	@Override
	public void setMass(double mass) {
		rigidBody.setMass(mass);
	}

	@Override
	public Vector3d velocity() {
		return rigidBody.velocity();
	}

	@Override
	public void setVelocity(Vector3d velocity) {
		rigidBody.setVelocity(velocity);
	}

	@Override
	public double drag() {
		return rigidBody.drag();
	}

	@Override
	public void setDrag(double drag) {
		rigidBody.setDrag(drag);
	}

	@Override
	public Vector3d gravity() {
		return rigidBody.gravity();
	}

	@Override
	public void setGravity(Vector3d gravity) {
		rigidBody.setGravity(gravity);
	}

	@Override
	public double angularDrag() {
		return rigidBody.angularDrag();
	}

	@Override
	public void setAngularDrag(double angularDrag) {
		rigidBody.setAngularDrag(angularDrag);
	}

	@Override
	public Quaternion angularVelocity() {
		return rigidBody.angularVelocity();
	}

	@Override
	public void setAngularVelocity(Quaternion angularVelocity) {
		rigidBody.setAngularVelocity(angularVelocity);
	}

	@Override
	public Vector3d center() {
		return rigidBody.center();
	}

	@Override
	public void setCenter(Vector3d center) {
		rigidBody.setCenter(center);
	}

	@Override
	public void addForce(Vector3d force) {
		rigidBody.addForce(force);
	}

	@Override
	public void addForce(Vector3d force, Vector3d position) {
		rigidBody.addForce(force, position);
	}

	@Override
	public void addTorque(Vector3d torque) {
		rigidBody.addTorque(torque);
	}
}
