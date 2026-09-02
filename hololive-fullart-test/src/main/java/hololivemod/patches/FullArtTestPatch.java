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
 * Full-art rendering test for Hololive_Strike.
 *
 * Gameplay is untouched. This patch only replaces the visual card surface.
 * Normal cards use the old Rindou/Foundation full-art baseline of 296x414.
 * Single-card view uses 600x840. The source image keeps its aspect ratio
 * and is CONTAIN-fit, never stretched or cropped.
 */
public final class FullArtTestPatch {
    private static final String TEST_CARD_ID = "Hololive_Strike";
    private static final String TEST_ART_PATH = "img/card/fullart/fullart_test.png";

    private static final float NORMAL_BASE_W = 300.0F;
    private static final float NORMAL_BASE_H = 420.0F;
    private static final float NORMAL_ART_W = 296.0F;
    private static final float NORMAL_ART_H = 414.0F;

    private static final float POPUP_BASE_W = 608.0F;
    private static final float POPUP_BASE_H = 848.0F;
    private static final float POPUP_ART_W = 600.0F;
    private static final float POPUP_ART_H = 840.0F;

    private static final float NORMAL_DESC_W = 270.0F;
    private static final float NORMAL_DESC_H = 112.0F;
    private static final float NORMAL_DESC_Y = -126.0F;
    private static final float NORMAL_TITLE_W = 236.0F;
    private static final float NORMAL_TITLE_H = 38.0F;
    private static final float NORMAL_TITLE_Y = 174.0F;

    private static final float POPUP_DESC_W = 548.0F;
    private static final float POPUP_DESC_H = 224.0F;
    private static final float POPUP_DESC_Y = -252.0F;
    private static final float POPUP_TITLE_W = 480.0F;
    private static final float POPUP_TITLE_H = 76.0F;
    private static final float POPUP_TITLE_Y = 348.0F;

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

    private static void drawCardTexture(SpriteBatch sb,
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
        drawCardTexture(sb, card, texture, 0.0F,
                texture.getWidth() * scale,
                texture.getHeight() * scale,
                color);
    }

    private static void drawNormalSurface(SpriteBatch sb, AbstractCard card) {
        float alpha = card.transparency;

        // A neutral 2px-ish edge replaces the colored vanilla card body.
        drawCardTexture(sb, card, ImageMaster.WHITE_SQUARE_IMG, 0.0F,
                NORMAL_BASE_W, NORMAL_BASE_H,
                new Color(0.035F, 0.035F, 0.045F, alpha));

        // Rindou/Foundation full-art baseline: preserve aspect ratio, CONTAIN fit.
        drawCardContain(sb, card, getTestArt(), NORMAL_ART_W, NORMAL_ART_H,
                new Color(1.0F, 1.0F, 1.0F, alpha));

        // Minimal readability overlays only. No blue vanilla body/frame/banner.
        drawCardTexture(sb, card, ImageMaster.WHITE_SQUARE_IMG, NORMAL_TITLE_Y,
                NORMAL_TITLE_W, NORMAL_TITLE_H,
                new Color(0.0F, 0.0F, 0.0F, 0.42F * alpha));
        drawCardTexture(sb, card, ImageMaster.WHITE_SQUARE_IMG, NORMAL_DESC_Y,
                NORMAL_DESC_W, NORMAL_DESC_H,
                new Color(0.0F, 0.0F, 0.0F, 0.56F * alpha));

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

    private static void drawPopupSurface(SpriteBatch sb) {
        float cx = Settings.WIDTH / 2.0F;
        float cy = Settings.HEIGHT / 2.0F;

        drawScreenTexture(sb, ImageMaster.WHITE_SQUARE_IMG, cx, cy,
                POPUP_BASE_W, POPUP_BASE_H,
                new Color(0.035F, 0.035F, 0.045F, 1.0F));
        drawScreenContain(sb, getTestArt(), cx, cy,
                POPUP_ART_W, POPUP_ART_H,
                Color.WHITE);
        drawScreenTexture(sb, ImageMaster.WHITE_SQUARE_IMG, cx,
                cy + POPUP_TITLE_Y * Settings.scale,
                POPUP_TITLE_W, POPUP_TITLE_H,
                new Color(0.0F, 0.0F, 0.0F, 0.42F));
        drawScreenTexture(sb, ImageMaster.WHITE_SQUARE_IMG, cx,
                cy + POPUP_DESC_Y * Settings.scale,
                POPUP_DESC_W, POPUP_DESC_H,
                new Color(0.0F, 0.0F, 0.0F, 0.56F));
        sb.setColor(Color.WHITE);
    }

    /**
     * Replace the complete vanilla image layer, not just the portrait.
     * AbstractCard renders type/title/description/cost afterwards, so those stay intact.
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
            drawNormalSurface(sb, __instance);
            return SpireReturn.Return(null);
        }
    }

    /** Replace the large-view card body with the same full-art rule. */
    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderCardBack")
    public static class PopupCardBackPatch {
        public static SpireReturn<Void> Prefix(SingleCardViewPopup __instance, SpriteBatch sb) {
            if (!applies(popupCard(__instance))) {
                return SpireReturn.Continue();
            }
            drawPopupSurface(sb);
            return SpireReturn.Return(null);
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderPortrait")
    public static class PopupPortraitPatch {
        public static SpireReturn<Void> Prefix(SingleCardViewPopup __instance, SpriteBatch sb) {
            return applies(popupCard(__instance)) ? SpireReturn.Return(null) : SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderFrame")
    public static class PopupFramePatch {
        public static SpireReturn<Void> Prefix(SingleCardViewPopup __instance, SpriteBatch sb) {
            return applies(popupCard(__instance)) ? SpireReturn.Return(null) : SpireReturn.Continue();
        }
    }

    @SpirePatch(clz = SingleCardViewPopup.class, method = "renderCardBanner")
    public static class PopupBannerPatch {
        public static SpireReturn<Void> Prefix(SingleCardViewPopup __instance, SpriteBatch sb) {
            return applies(popupCard(__instance)) ? SpireReturn.Return(null) : SpireReturn.Continue();
        }
    }
}
