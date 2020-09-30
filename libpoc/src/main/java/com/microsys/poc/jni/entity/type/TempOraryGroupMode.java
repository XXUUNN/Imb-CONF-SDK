package com.microsys.poc.jni.entity.type;

public enum TempOraryGroupMode {
	UKNOWN,
	DYNAMIC,
	TEMP;
	
	public static TempOraryGroupMode of(int value) {
		TempOraryGroupMode type;
		
		switch(value) {
			case 0: type = TempOraryGroupMode.UKNOWN;
					break;
			case 1: type = TempOraryGroupMode.DYNAMIC;
					break;
			case 2: type = TempOraryGroupMode.TEMP;
			        break;
			default: type = TempOraryGroupMode.UKNOWN;
					break;
		}
		
		return type;
	}
	
	public static int getTypeof(TempOraryGroupMode tempOraryGroupMode) {
		int type;
		
		switch(tempOraryGroupMode) {
		
			case UKNOWN: type = 0;
					break;
			case DYNAMIC: type = 1;
					break;
			case TEMP : type = 2;
			        break;
			default: type = 0;
					break;
		}
		
		return type;
	}

}
