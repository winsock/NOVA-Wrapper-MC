package nova.wrapper.mc18.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import nova.core.entity.component.Player;
import nova.core.network.Packet;

/**
 * Wraps ByteBuf into a NOVA Packet.
 * @author Calclavia
 */
public class MCPacket implements Packet {

	public final ByteBuf buf;
	public final Player player;
	private int id = 0;

	public MCPacket(ByteBuf buf) {
		this.buf = buf;
		player = null;
	}

	public MCPacket(ByteBuf buf, Player player) {
		this.buf = buf;
		this.player = player;
	}

	@Override
	public Player player() {
		if (player == null) {
			throw new RuntimeException("Attempt to get player in packet when it does not exist!");
		}

		return player;
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public Packet setID(int id) {
		this.id = id;
		return this;
	}

	@Override
	public Packet writeBoolean(boolean value) {
		buf.writeBoolean(value);
		return this;
	}

	@Override
	public Packet writeByte(int value) {
		buf.writeByte(value);
		return this;
	}

	@Override
	public Packet writeShort(int value) {
		buf.writeShort(value);
		return this;
	}

	@Override
	public Packet writeInt(int value) {
		buf.writeInt(value);
		return this;
	}

	@Override
	public Packet writeLong(long value) {
		buf.writeLong(value);
		return this;
	}

	@Override
	public Packet writeChar(int value) {
		buf.writeChar(value);
		return this;
	}

	@Override
	public Packet writeFloat(float value) {
		buf.writeFloat(value);
		return this;
	}

	@Override
	public Packet writeDouble(double value) {
		buf.writeDouble(value);
		return this;
	}

	@Override
	public Packet writeString(String value) {
		ByteBufUtils.writeUTF8String(buf, value);
		return this;
	}

	@Override
	public Packet writeBytes(byte[] array) {
		buf.writeBytes(array);
		return this;
	}

	@Override
	public byte[] readBytes(int length) {
		byte[] array = new byte[length];
		for (int i = 0; i < length; i++)
			array[i] = buf.readByte();
		return array;
	}

	@Override
	public boolean readBoolean() {
		return buf.readBoolean();
	}

	@Override
	public byte readByte() {
		return buf.readByte();
	}

	@Override
	public short readUnsignedByte() {
		return buf.readUnsignedByte();
	}

	@Override
	public short readShort() {
		return buf.readShort();
	}

	@Override
	public int readInt() {
		return buf.readInt();
	}

	@Override
	public long readUnsignedInt() {
		return buf.readUnsignedInt();
	}

	@Override
	public long readLong() {
		return buf.readLong();
	}

	@Override
	public char readChar() {
		return buf.readChar();
	}

	@Override
	public float readFloat() {
		return buf.readFloat();
	}

	@Override
	public double readDouble() {
		return buf.readDouble();
	}

	@Override
	public String readString() {
		return ByteBufUtils.readUTF8String(buf);
	}

	public void writeTo(ByteBuf other) {
		other.writeBytes(buf);
	}
}
