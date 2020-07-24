package by.dero.gvh.fireworks;

import org.bukkit.inventory.ItemStack;

public class jDataWatcherObject<T> implements iNmsObject{
	
	private static int version;
	
	static{
		String v=ProtocolUtils.version;
		if(v.startsWith("v1_9")){
			if(v.endsWith("R1"))jDataWatcherObject.version=0;
			else jDataWatcherObject.version=1;
		}
		else if(v.startsWith("v1_10")){
			jDataWatcherObject.version=2;
		}
		version = 3;
	}
	private static String v(String...values){
		return values[jDataWatcherObject.version];
	}
	
	public static final jDataWatcherObject<Byte> entity_byte=new jDataWatcherObject<>(v("ax", "ay", "aa", "Z"), "Entity");
	public static final jDataWatcherObject<Integer> entity_int=new jDataWatcherObject<>(v("ay", "az", "az", "aA"), "Entity");
	public static final jDataWatcherObject<String> entity_string=new jDataWatcherObject<>(v("az", "aA", "aA", "aB"), "Entity");
	public static final jDataWatcherObject<Boolean> entity_boolean1=new jDataWatcherObject<>(v("aA", "aB", "aB", "aC"), "Entity");
	public static final jDataWatcherObject<Boolean> entity_boolean2=new jDataWatcherObject<>(v("aB", "aC", "aC", "aD"), "Entity");
	public static final jDataWatcherObject<Boolean> entity_boolean3=new jDataWatcherObject<>(v(null, null, "aD", "aE"), "Entity");
	public static final jDataWatcherObject<net.minecraft.server.v1_12_R1.ItemStack> entityfireworks_itemstack=new jDataWatcherObject<>("FIREWORK_ITEM", "EntityFireworks");
	
	
	
	private final Object nms;
	
	private jDataWatcherObject(String fieldname, String classname){
		this.nms=ProtocolUtils.refl_fieldGet0(fieldname, classname);
	}
	
	/**
	 * Build
	 */
	@Override
	public Object build(){
		return this.nms;
	}
}
