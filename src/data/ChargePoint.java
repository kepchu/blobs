package data;

//attraction/repulsion point
public class ChargePoint {

	enum ChargeType {
		R, G, B, BLACK, WHITE
	}
	
	private Vec position;
	private double power;
	private ChargeType chargeType;
	
	//cloning constructor
	
	public ChargePoint(ChargePoint c) {
		this.position = c.position;
		this.power = c.power;
		this.chargeType = c.chargeType;
	}
	public ChargePoint(Vec position, double power, ChargeType chargeType) {
		super();
		this.position = position;
		this.power = power;
		this.chargeType = chargeType;
	}
	public ChargePoint(Vec position, double power) {
		this(position, power, ChargeType.R);
	}
	public ChargePoint(Vec position) {
		this(position, 1.0, ChargeType.R);
	}
	
	public void setChargeType (ChargeType type) {
		this.chargeType = type;
	}
	
	public ChargeType getChargeType () {
		return this.chargeType;
	}
	
	public Vec getPosition() {
		return position;
	}
	public void setPosition (Vec position) {
		this.position = position;
	}
	public double getPower() {
		return power;
	}
	public void setPower(double power) {
		this.power = power;
	}
}
