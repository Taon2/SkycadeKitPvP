package net.skycade.kitpvp.ui;

import net.skycade.kitpvp.coreclasses.utils.UtilString;

public enum MenuSize {

	ONE_LINE, TWO_LINE, THREE_LINE, FOUR_LINE, FIVE_LINE, SIX_LINE;

	private int lines;

	MenuSize() {
		this.lines = -1;
	}

	public int getLines() {
		if (lines == -1)
			for (int i = 0; i < values().length; i++)
				if (values()[i] == this)
					lines = i + 1;
		return lines;
	}

	public int getSize() {
		return getLines() * 9;
	}

	@Override
	public String toString() {
		return String.valueOf(getSize());
	}

	public String getName() {
		return UtilString.capitaliseString(name().replace("_", " ") + (getLines() > 1 ? "s" : ""));
	}

}