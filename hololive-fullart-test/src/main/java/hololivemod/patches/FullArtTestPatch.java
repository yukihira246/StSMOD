package hololivemod.patches;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

/**
 * Temporary rendering-only test for Hololive_Strike.
 *
 * No card behavior, cost, damage, rarity, tags, or upgrade logic is changed.
 * The supplied test picture must be stored at:
 * img/card/fullart/fullart_test.png
 */
public final class FullArtTestPatch {
    private static final String TEST_CARD_ID = "Hololive_Strike";
    private static final String TEST_ART_PATH = "img/card/fullart/fullart_test.png";

    // Keep a small inset for test 1 so the square image corners do not protrude
    // outside Slay the Spire's rounded card silhouette.
    private static final float ART_W = 272.0F;
    private static final float ART_H = 392.0F;

    // Dark readability band behind the normal description text.
    private static final float DESC_PANEL_W = 258.0F;
    private static final float DESC_PANEL_H = 126.0F;
    private static final float DESC_PANEL_Y = -128.0F;

    private static Texture testArt;

    private FullArtTestPatch() {
    }

    private static boolean applies(AbstractCard card) {
        return card != null && TEST_CARD_ID.equals(card.cardID);
    }

    private static Texture getTestArt() {
        if (testArt == null) {
            testArt = ImageMaster.loadImage(TEST_ART_PATH);
        }
        return testArt;
    }

    private static void drawCentered(SpriteBatch sb,
                                     AbstractCard card,
                                     Texture texture,
                                     float localCenterY,
                                     float rawWidth,
                                     float rawHeight,
                                     Color color) {
        if (texture == null) {
            return;
        }

        float width = rawWidth * Settings.scale;
        float height = rawHeight * Settings.scale;

        // Rotate the local Y offset together with the card.
        double radians = Math.toRadians(card.angle);
        float offset = localCenterY * Settings.scale * card.drawScale;
        float centerX = card.current_x - (float) Math.sin(radians) * offset;
        float centerY = card.current_y + (float) Math.cos(radians) * offset;

        sb.setColor(color);
        sb.draw(texture,
                centerX - width / 2.0F,
                centerY - height / 2.0F,
                width / 2.0F,
                height / 2.0F,
                width,
                height,
                card.drawScale,
                card.drawScale,
                card.angle,
                0,
                0,
                texture.getWidth(),
                texture.getHeight(),
                false,
                false);
    }

    /** Draw the supplied vertical picture over the normal card background. */
    @SpirePatch(clz = AbstractCard.class, method = "renderCardBg")
    public static class RenderCardBgPatch {
        public static void Postfix(AbstractCard __instance, SpriteBatch sb, float x, float y) {
            if (!applies(__instance)) {
                return;
            }

            float alpha = __instance.transparency;
            drawCentered(sb,
                    __instance,
                    getTestArt(),
                    0.0F,
                    ART_W,
                    ART_H,
                    new Color(1.0F, 1.0F, 1.0F, alpha));

            // Keep vanilla title/type/description/energy rendering, but give the
            // description a translucent backing so test art cannot make it unreadable.
            drawCentered(sb,
                    __instance,
                    ImageMaster.WHITE_SQUARE_IMG,
                    DESC_PANEL_Y,
                    DESC_PANEL_W,
                    DESC_PANEL_H,
                    new Color(0.0F, 0.0F, 0.0F, 0.52F * alpha));

            sb.setColor(Color.WHITE);
        }
    }

    /** Prevent the ordinary 250x190 portrait from covering the full-art picture. */
    @SpirePatch(clz = AbstractCard.class, method = "renderPortrait")
    public static class RenderPortraitPatch {
        public static SpireReturn<Void> Prefix(AbstractCard __instance, SpriteBatch sb) {
            if (applies(__instance)) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    /** Prevent the normal portrait window/frame from cutting through the full art. */
    @SpirePatch(clz = AbstractCard.class, method = "renderPortraitFrame")
    public static class RenderPortraitFramePatch {
        public static SpireReturn<Void> Prefix(AbstractCard __instance, SpriteBatch sb, float x, float y) {
            if (applies(__instance)) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }

    /** Keep the test stable even if playtester/beta-art mode is enabled. */
    @SpirePatch(clz = AbstractCard.class, method = "renderJokePortrait")
    public static class RenderJokePortraitPatch {
        public static SpireReturn<Void> Prefix(AbstractCard __instance, SpriteBatch sb) {
            if (applies(__instance)) {
                return SpireReturn.Return(null);
            }
            return SpireReturn.Continue();
        }
    }
}
