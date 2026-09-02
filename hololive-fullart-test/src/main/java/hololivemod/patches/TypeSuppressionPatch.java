package hololivemod.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

/**
 * Rindou-style type labels are part of the transparent frame itself.
 * Suppress only the vanilla centered type label for the Strike layout test.
 */
public final class TypeSuppressionPatch {
    private static final String TEST_CARD_ID = "Hololive_Strike";

    private TypeSuppressionPatch() {
    }

    private static boolean applies(AbstractCard card) {
        return card != null && TEST_CARD_ID.equals(card.cardID);
    }

    private static AbstractCard popupCard(SingleCardViewPopup popup) {
        return ReflectionHacks.getPrivate(popup, SingleCardViewPopup.class, "card");
    }

    @SpirePatch(clz = AbstractCard.class, method = "renderType")
    public static class NormalTypePatch {
        public static SpireReturn<Void> Prefix(AbstractCard __instance, SpriteBatch sb) {
            return applies(__instance) ? SpireReturn.Return(null) : SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderCardTypeText")
    public static class PopupTypePatch {
        public static SpireReturn<Void> Prefix(SingleCardViewPopup __instance, SpriteBatch sb) {
            return applies(popupCard(__instance)) ? SpireReturn.Return(null) : SpireReturn.Continue();
        }
    }
}
