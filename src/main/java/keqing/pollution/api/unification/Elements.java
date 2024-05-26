package keqing.pollution.api.unification;

import gregtech.api.unification.Element;

import java.util.HashMap;
import java.util.Map;

import static gregtech.api.unification.Elements.add;

public class Elements {

	private static final Map<String, Element> elements = new HashMap<>();

	private Elements() {
	}

	public static final Element Ae = add(1, 1, -1, null, "Air", "Ae", false);
	public static final Element Ig = add(1, 2, -1, null, "Fire", "Ig", false);
	public static final Element Aq = add(1, 3, -1, null, "Water", "Aq", false);
	public static final Element Ter = add(1, 4, -1, null, "Earth", "Ter", false);
	public static final Element Pe = add(1, 5, -1, null, "Entropy", "Pe", false);
	public static final Element Ord = add(1, 6, -1, null, "Order", "Ord", false);
	public static final Element Ma = add(1, 7, -1, null, "Mana", "Ma", false);
	public static final Element Su = add(1, 8, -1, null, "Sunnarium", "Su", false);
	public static final Element Wma = add(1, 9, -1, null, "Whitemansus", "Wma", false);
	public static final Element Bma = add(1, 10, -1, null, "Blackmansus", "Bma", false);
}