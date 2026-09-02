package hololivemod.patches;

import basemod.ReflectionHacks;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DescriptionLine;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

/**
 * Layout-only test for Hololive_Strike.
 *
 * Vanilla description rendering is preserved; only DescriptionLine.width is
 * temporarily normalized so each rendered line starts from a shared left edge
 * instead of being centered independently.
 */
public final class DescriptionAlignmentPatch {
    private static final String TEST_CARD_ID = "Hololive_Strike";

    // v0.0.6: move normal-size text slightly right compared with v0.0.5.
    private static final float NORMAL_FORCED_WIDTH = 210.0F;

    // Large popup placement already looked good in v0.0.5, so keep it unchanged.
    private static final float POPUP_FORCED_WIDTH = 230.0F;

    private static float[] normalBackup;
    private static float[] popupBackup;

    private DescriptionAlignmentPatch() {
    }

    private static boolean applies(AbstractCard card) {
        return card != null && TEST_CARD_ID.equals(card.cardID);
    }

    private static AbstractCard popupCard(SingleCardViewPopup popup) {
        return ReflectionHacks.getPrivate(popup, SingleCardViewPopup.class, "card");
    }

    private static float[] forceWidth(AbstractCard card, float width) {
        if (card == null || card.description == null) {
            return null;
        }
        float[] backup = new float[card.description.size()];
        for (int i = 0; i < card.description.size(); i++) {
            DescriptionLine line = card.description.get(i);
            backup[i] = line.width;
            line.width = width * Settings.scale;
        }
        return backup;
    }

    private static void restoreWidth(AbstractCard card, float[] backup) {
        if (card == null || card.description == null || backup == null) {
            return;
        }
        int count = Math.min(card.description.size(), backup.length);
        for (int i = 0; i < count; i++) {
            card.description.get(i).width = backup[i];
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "renderDescription")
    public static class NormalDescriptionPatch {
        public static void Prefix(AbstractCard __instance) {
            if (applies(__instance)) {
                normalBackup = forceWidth(__instance, NORMAL_FORCED_WIDTH);
            }
        }

        public static void Postfix(AbstractCard __instance) {
            if (applies(__instance)) {
                restoreWidth(__instance, normalBackup);
                normalBackup = null;
            }
        }
    }

    @SpirePatch(clz = AbstractCard.class, method = "renderDescriptionCN")
    public static class NormalDescriptionCNPatch {
        public static void Prefix(AbstractCard __instance) {
            if (applies(__instance)) {
                normalBackup = forceWidth(__instance, NORMAL_FORCED_WIDTH);
            }
        }

        public static void Postfix(AbstractCard __instance) {
            if (applies(__instance)) {
                restoreWidth(__instance, normalBackup);
                normalBackup = null;
            }
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderDescription")
    public static class PopupDescriptionPatch {
        public static void Prefix(SingleCardViewPopup __instance) {
            AbstractCard card = popupCard(__instance);
            if (applies(card)) {
                popupBackup = forceWidth(card, POPUP_FORCED_WIDTH);
            }
        }

        public static void Postfix(SingleCardViewPopup __instance) {
            AbstractCard card = popupCard(__instance);
            if (applies(card)) {
                restoreWidth(card, popupBackup);
                popupBackup = null;
            }
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderDescriptionCN")
    public static class PopupDescriptionCNPatch {
        public static void Prefix(SingleCardViewPopup __instance) {
            AbstractCard card = popupCard(__instance);
            if (applies(card)) {
                popupBackup = forceWidth(card, POPUP_FORCED_WIDTH);
            }
        }

        public static void Postfix(SingleCardViewPopup __instance) {
            AbstractCard card = popupCard(__instance);
            if (applies(card)) {
                restoreWidth(card, popupBackup);
                popupBackup = null;
            }
        }
    }
}
