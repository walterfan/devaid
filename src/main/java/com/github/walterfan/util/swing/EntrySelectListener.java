package com.github.walterfan.util.swing;

import java.util.EventListener;

public interface EntrySelectListener extends EventListener {
	void onSelect(Object key, Object value);
}
