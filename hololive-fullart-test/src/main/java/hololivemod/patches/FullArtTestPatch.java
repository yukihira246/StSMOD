package hololivemod.patches;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;

/**
 * Foundation/Rindou-style layered full-art test for Hololive_Strike.
 *
 * Gameplay is untouched. The visual surface is composed as:
 *   art -> transparent frame -> transparent rarity marker -> vanilla text/cost.
 *
 * Foundation baseline:
 *   normal art : 296 x 414
 *   detail art : 600 x 840
 *   frame      : 512 x 512 / 1024 x 1024 transparent PNG
 */
public final class FullArtTestPatch {
    private static final String TEST_CARD_ID = "Hololive_Strike";

    private static final String ART_PATH = "img/card/fullart/fullart_test.png";
    private static final String FRAME_512_PATH = "img/card/fullart/frame_attack_512.png";
    private static final String FRAME_1024_PATH = "img/card/fullart/frame_attack_1024.png";
    private static final String RARITY_512_PATH = "img/card/fullart/rarity_special_512.png";
    private static final String RARITY_1024_PATH = "img/card/fullart/rarity_special_1024.png";

    private static final float NORMAL_BASE_W = 300.0F;
    private static final float NORMAL_BASE_H = 420.0F;
    private static final float NORMAL_ART_W = 296.0F;
    private static final float NORMAL_ART_H = 414.0F;
    private static final float NORMAL_OVERLAY_SIZE = 512.0F;

    private static final float POPUP_BASE_W = 608.0F;
    private static final float POPUP_BASE_H = 848.0F;
    private static final float POPUP_ART_W = 600.0F;
    private static final float POPUP_ART_H = 840.0F;
    private static final float POPUP_OVERLAY_SIZE = 1024.0F;

    private static Texture art;
    private static Texture frame512;
    private static Texture frame1024;
    private static Texture rarity512;
    private static Texture rarity1024;

    private FullArtTestPatch() {
    }

    private static boolean applies(AbstractCard card) {
        return card != null && TEST_CARD_ID.equals(card.cardID);
    }

    private static Texture load(Texture current, String path) {
        return current != null ? current : ImageMaster.loadImage(path);
    }

    private static Texture art() {
        art = load(art, ART_PATH);
        return art;
    }

    private static Texture frame512() {
        frame512 = load(frame512, FRAME_512_PATH);
        return frame512;
    }

    private static Texture frame1024() {
        frame1024 = load(frame1024, FRAME_1024_PATH);
        return frame1024;
    }

    private static Texture rarity512() {
        rarity512 = load(rarity512, RARITY_512_PATH);
        return rarity512;
    }

    private static Texture rarity1024() {
        rarity1024 = load(rarity1024, RARITY_1024_PATH);
        return rarity1024;
    }

    private static void drawCardTexture(SpriteBatch sb,
                                        AbstractCard card,
                                        Texture texture,
                                        float rawWidth,
                                        float rawHeight,
                                        Color color) {
        if (texture == null) {
            return;
        }

        float width = rawWidth * Settings.scale;
        float height = rawHeight * Settings.scale;
        sb.setColor(color);
        sb.draw(texture,
                card.current_x - width / 2.0F,
                card.current_y - height / 2.0F,
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

    private static void drawCardContain(SpriteBatch sb,
                                        AbstractCard card,
                                        Texture texture,
                                        float boxW,
                                        float boxH,
                                        Color color) {
        if (texture == null || texture.getWidth() <= 0 || texture.getHeight() <= 0) {
            return;
        }
        float scale = Math.min(boxW / texture.getWidth(), boxH / texture.getHeight());
        drawCardTexture(sb, card, texture,
                texture.getWidth() * scale,
                texture.getHeight() * scale,
                color);
    }

    private static void drawNormalArt(SpriteBatch sb, AbstractCard card) {
        float alpha = card.transparency;

        // Neutral underlay only covers any letterbox area left by CONTAIN fitting.
        drawCardTexture(sb, card, ImageMaster.WHITE_SQUARE_IMG,
                NORMAL_BASE_W, NORMAL_BASE_H,
                new Color(0.018F, 0.022F, 0.030F, alpha));

        // Full art first.
        drawCardContain(sb, card, art(), NORMAL_ART_W, NORMAL_ART_H,
                new Color(1.0F, 1.0F, 1.0F, alpha));

        sb.setColor(Color.WHITE);
    }

    private static void drawNormalOverlay(SpriteBatch sb, AbstractCard card) {
        float alpha = card.transparency;
        Color white = new Color(1.0F, 1.0F, 1.0F, alpha);

        // Foundation model: frame and rarity are independent transparent layers.
        drawCardTexture(sb, card, frame512(), NORMAL_OVERLAY_SIZE, NORMAL_OVERLAY_SIZE, white);
        drawCardTexture(sb, card, rarity512(), NORMAL_OVERLAY_SIZE, NORMAL_OVERLAY_SIZE, white);
        sb.setColor(Color.WHITE);
    }

    private static void drawScreenTexture(SpriteBatch sb,
                                          Texture texture,
                                          float centerX,
                                          float centerY,
                                          float rawWidth,
                                          float rawHeight,
                                          Color color) {
        if (texture == null) {
            return;
        }
        float width = rawWidth * Settings.scale;
        float height = rawHeight * Settings.scale;
        sb.setColor(color);
        sb.draw(texture,
                centerX - width / 2.0F,
                centerY - height / 2.0F,
                width / 2.0F,
                height / 2.0F,
                width,
                height,
                1.0F,
                1.0F,
                0.0F,
                0,
                0,
                texture.getWidth(),
                texture.getHeight(),
                false,
                false);
    }

    private static void drawScreenContain(SpriteBatch sb,
                                          Texture texture,
                                          float centerX,
                                          float centerY,
                                          float boxW,
                                          float boxH,
                                          Color color) {
        if (texture == null || texture.getWidth() <= 0 || texture.getHeight() <= 0) {
            return;
        }
        float scale = Math.min(boxW / texture.getWidth(), boxH / texture.getHeight());
        drawScreenTexture(sb, texture, centerX, centerY,
                texture.getWidth() * scale,
                texture.getHeight() * scale,
                color);
    }

    private static AbstractCard popupCard(SingleCardViewPopup popup) {
        return ReflectionHacks.getPrivate(popup, SingleCardViewPopup.class, "card");
    }

    private static void drawPopupArt(SpriteBatch sb) {
        float cx = Settings.WIDTH / 2.0F;
        float cy = Settings.HEIGHT / 2.0F;

        drawScreenTexture(sb, ImageMaster.WHITE_SQUARE_IMG, cx, cy,
                POPUP_BASE_W, POPUP_BASE_H,
                new Color(0.018F, 0.022F, 0.030F, 1.0F));
        drawScreenContain(sb, art(), cx, cy,
                POPUP_ART_W, POPUP_ART_H,
                Color.WHITE);
        sb.setColor(Color.WHITE);
    }

    private static void drawPopupOverlay(SpriteBatch sb) {
        float cx = Settings.WIDTH / 2.0F;
        float cy = Settings.HEIGHT / 2.0F;
        drawScreenTexture(sb, frame1024(), cx, cy,
                POPUP_OVERLAY_SIZE, POPUP_OVERLAY_SIZE,
                Color.WHITE);
        drawScreenTexture(sb, rarity1024(), cx, cy,
                POPUP_OVERLAY_SIZE, POPUP_OVERLAY_SIZE,
                Color.WHITE);
        sb.setColor(Color.WHITE);
    }

    /**
     * Replace the complete vanilla image layer. Text/type/description/cost are
     * rendered by AbstractCard afterwards, so they stay at the standard positions.
     */
    @SpirePatch(clz = AbstractCard.class, method = "renderImage")
    public static class NormalRenderImagePatch {
        public static SpireReturn<Void> Prefix(AbstractCard __instance,
                                               SpriteBatch sb,
                                               boolean hovered,
                                               boolean selected) {
            if (!applies(__instance)) {
                return SpireReturn.Continue();
            }
            drawNormalArt(sb, __instance);
            drawNormalOverlay(sb, __instance);
            return SpireReturn.Return(null);
        }
    }

    /** Large-view art layer. */
    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderCardBack")
    public static class PopupCardBackPatch {
        public static SpireReturn<Void> Prefix(SingleCardViewPopup __instance, SpriteBatch sb) {
            if (!applies(popupCard(__instance))) {
                return SpireReturn.Continue();
            }
            drawPopupArt(sb);
            return SpireReturn.Return(null);
        }
    }

    /** Normal portrait is already represented by the full art. */
    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderPortrait")
    public static class PopupPortraitPatch {
        public static SpireReturn<Void> Prefix(SingleCardViewPopup __instance, SpriteBatch sb) {
            return applies(popupCard(__instance)) ? SpireReturn.Return(null) : SpireReturn.Continue();
        }
    }

    /** Draw the independent 1024 frame + rarity layers at the frame stage. */
    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderFrame")
    public static class PopupFramePatch {
        public static SpireReturn<Void> Prefix(SingleCardViewPopup __instance, SpriteBatch sb) {
            if (!applies(popupCard(__instance))) {
                return SpireReturn.Continue();
            }
            drawPopupOverlay(sb);
            return SpireReturn.Return(null);
        }
    }

    /** Rarity is now its own layer, so suppress the vanilla banner. */
    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderCardBanner")
    public static class PopupBannerPatch {
        public static SpireReturn<Void> Prefix(SingleCardViewPopup __instance, SpriteBatch sb) {
            return applies(popupCard(__instance)) ? SpireReturn.Return(null) : SpireReturn.Continue();
        }
    }
}
