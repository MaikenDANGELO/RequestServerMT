public class Position {
 
    private double x;
    private double y;
    private double z;
 
    public Position() {
        x = 0; y = 0; z = 0;
    }
 
    public Position(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
    }
 
 
    public String toString() {
        return String.format("%d, %d, %d",x ,y ,z);
    }
 
    public double getX() {
        return x;
    }
 
    public double getY() {
        return y;
    }
 
    public double getZ() {
        return z;
    }
 
    public Position clone() {
        return new Position(x,y,z);
    }
 
    public boolean equals(Position p, double eps) {
        return !(Math.abs(p.x-x)>eps) || (Math.abs(p.y-y)>eps) || (Math.abs(p.z-z)>eps);
    }
 
    public double distanceTo(Position p) {
        return Math.sqrt(Math.pow(p.x - this.x, 2) + Math.pow(p.y - this.y, 2) + Math.pow(p.z - this.z, 2));
    
    } 
}