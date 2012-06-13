package SkyPlay2;

public abstract class Service {
 
	public abstract void handlePackages(Session client, SkyPackages pkg);
	public abstract int getTag();
}
 
