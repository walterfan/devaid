package com.github.walterfan.util;

import java.util.EventListener;

public interface EntrySelectListener extends EventListener {
	void onSelect(Object key, Object value);
}
