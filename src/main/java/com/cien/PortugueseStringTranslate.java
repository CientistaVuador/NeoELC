package com.cien;

import java.util.IllegalFormatException;

import net.minecraft.util.StringTranslate;

public class PortugueseStringTranslate extends StringTranslate {

	public static final PortugueseStringTranslate PORTUGUESE = new PortugueseStringTranslate();
	protected static StringTranslate FALLBACK = null;
	
	private PortugueseStringTranslate() {
		super();
	}
	
    public synchronized String translateKey(String p_74805_1_)
    {
        String s = Util.translateUnlocalizedToPortuguese(p_74805_1_);
        if (s == null && FALLBACK != null) {
        	return FALLBACK.translateKey(p_74805_1_);
        }
        return s;
    }

    private String tryTranslateKey(String p_135064_1_)
    {
        String s1 = Util.translateUnlocalizedToPortuguese(p_135064_1_);
        if (s1 == null && FALLBACK != null) {
        	s1 = FALLBACK.translateKey(p_135064_1_);
        }
        return s1 == null ? p_135064_1_ : s1;
    }
 
    public synchronized String translateKeyFormat(String p_74803_1_, Object ... p_74803_2_)
    {
        String s1 = this.tryTranslateKey(p_74803_1_);

        try
        {
            return String.format(s1, p_74803_2_);
        }
        catch (IllegalFormatException illegalformatexception)
        {
            return "Format error: " + s1;
        }
    }
}
