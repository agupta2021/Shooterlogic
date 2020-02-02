
import java.lang.Math;

/**
 * Calculates ideal flywheel rpm and launch angle setpoints for projectile with drag based on user input
 * User input is final y-component of velocity 
 * @author Aditya Gupta
 */

public class Shooter {
    private static double LLtoPort = 1.58114915; //meters
    private static double shooterToPort = 1.46685;
    private static double xDist;
    private static double g = 9.8;
    private static double v0x;
    private static double v0y;
    private static double thetaLaunch;
    private static double RPM;
    private static double radius = 0.0508; //meters
    private static double rpmConversionFactor = 0.10472;
    private static double timeOfTrajectory;
    private static double vfy;
    private static double vfx;
    private static double thetaApproach = -5; //degrees
    private static double dragCoeff = 0.116; //subject to change
    private static double mass = 0.141748; //kilograms
    public static void main(String[] args) {
        //double ty = 22; //degrees ... source: LL
        //xDist = getX(ty);
        xDist = 5.1816; //meters
        vfy = 0.0001; //use getVfy eventually ... also dont make it exactly 0 cuz it returns NaN
              
        System.out.println("xDist " + xDist + " m");

        //vfy = getVfy(thetaApproach, v0x);
        System.out.println("vfy " + vfy + " m/s");

        v0y = getv0y(vfy);
        System.out.println("v0y " + v0y + " m/s");

        timeOfTrajectory = gettimeOfTraj(v0y); //need v0y here... but how
        System.out.println("time " + timeOfTrajectory + " s");

        vfx = getv0X(xDist, timeOfTrajectory);
        System.out.println("vfx " + vfx + " m/s");

        v0x = getv0xFromVfx(dragCoeff, timeOfTrajectory, vfx);
        System.out.println("v0x " + v0x + " m/s");

        thetaApproach = getThetaApproach(vfx, vfy);
        System.out.println("Approach Angle " + Math.toDegrees(thetaApproach) + " degrees");

        thetaLaunch = getThetaLaunch(v0x, v0y); //defaults to radians
        System.out.println("Launch Angle " + Math.toDegrees(thetaLaunch) + " degrees"); //shooter pitch setpoint

        RPM = getRPM(v0x, thetaLaunch); 
        System.out.println("Flywheel RPM " + RPM + " rpm"); //flywheel setpoint

       
    }

    public static double getX(double ty){
        return LLtoPort / Math.tan(Math.toRadians(ty));
    }

    public static double getTy(double xDist){
        return Math.atan(LLtoPort / xDist); //radians
    }

    public static double getv0X(double x, double t){
        return x / t;
    }

    public static double getv0xFromVfx(double dragCoeff, double timeOfTraj, double vfx){
        return vfx / (Math.pow(Math.E, (-dragCoeff * timeOfTraj) / mass));
    }

    public static double getVfX(double dragCoeff, double timeOfTraj, double v0x){
        return v0x * Math.pow(Math.E, (-dragCoeff * timeOfTraj) / mass);  
    }

    public static double getv0y(double vfy){
        double twogy = shooterToPort * 2 * g;
        return Math.sqrt((vfy * vfy) + twogy); //what if vfy is negative
    }

    public static double getThetaLaunch( double v0x, double v0y){
        return Math.atan(v0y/v0x);
    }

    public static double getThetaApproach(double vfx, double vfy){
        return Math.atan( vfy / vfx); //radians
    }

    public static double gettimeOfTraj(double v0y){
        double plusTime;
        double minusTime;
        double t1;
        double t2;
        plusTime = (v0y + Math.sqrt((v0y * v0y) - (4 * (g/2) * shooterToPort))) / g;
        minusTime = (v0y - Math.sqrt((v0y * v0y) - (4 * (g/2) * shooterToPort))) / g;

        if(plusTime <= minusTime){
            t1 = plusTime;
            t2 = minusTime;
        } else{
            t1 = minusTime;
            t2 = plusTime;
        }
       
        if (vfy >= 0){
            return t1; //first one
        } else {
            return t2; //second one
         } 
        
       
    }

    public static double getVfy(double thetaApproach, double v0x){ //need to use eventually
        return v0x * Math.tan(Math.toRadians(thetaApproach));
    }

    public static double getRPM(double v0x, double thetaLaunch){
        double numerator = Math.sqrt( (v0x *v0x) + ((v0x * Math.tan(thetaLaunch)) * (v0x * Math.tan(thetaLaunch))));
        double denominator = radius * rpmConversionFactor;
        return numerator / denominator;
    }

}