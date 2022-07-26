package me.hypherionmc.moonconfig.hocon;

/**
 * @author TheElectronWill
 */
public enum KeyValueSeparatorStyle {
	/**
	 * The : character.
	 */
	COLON(':', ' '),
	/**
	 * The = character.
	 */
	EQUALS(' ', '=', ' ');

	public final char[] chars;

	KeyValueSeparatorStyle(char... chars) {
		this.chars = chars;
	}
}
