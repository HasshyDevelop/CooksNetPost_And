package com.cooksnet.util;

import java.util.Locale;

import android.text.InputFilter;

public class MultibyteLengthFilter extends InputFilter.LengthFilter {
	public MultibyteLengthFilter(int ja, int zh, int elsee, Locale lang) {
//		super((Locale.JAPANESE.getLanguage().equals(lang.getLanguage())
//				|| Locale.CHINESE.getLanguage().equals(lang.getLanguage()) || Locale.KOREAN.getLanguage().equals(
//				lang.getLanguage())) ? max / 2 : max);
		super(Locale.JAPANESE.getLanguage().equals(lang.getLanguage()) ? ja : (Locale.CHINESE.getLanguage().equals(
				lang.getLanguage()) || Locale.KOREAN.getLanguage().equals(lang.getLanguage())) ? zh : elsee);
	}

}