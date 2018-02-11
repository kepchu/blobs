package data;

public class Command {
	public enum Com { VELOCITIES_MULTIPLY_BY, KILL_VELOCITIES, KILL_CHARGES, KILL_BLOBS, KILL_LAST_CHARGE,
		GRAVITY_CENTRE, GRAVITY_DOWN, SIDES_BOUNCY, SIDES_WRAPPED, VERTICALLY_WRAPPED,
		SWITCH_COLLISION_DETECTION, SWITCH_CHARGE_TYPES, SWITCH_CHARGE_COLOURS, SWITCH_TO_ALTERNATIVE_DATA}
	
	final Com commandType;
	final double doubleValue;
	final int value;
	
	public Command(Com commandType, double value) {
		super();
		this.commandType = commandType;
		this.doubleValue = value;
		this.value = (int)value;
	}
	public Command(Com commandType, int value) {
		super();
		this.commandType = commandType;
		this.doubleValue = value;
		this.value = value;
	}
	public Command (Com commandType) {
		this.commandType = commandType;
		this.doubleValue = this.value = 0;
	}
	
}
