package net.teamfruit.emojicord.compat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.NotImplementedException;

import com.mojang.blaze3d.platform.TextureUtil;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.VersionChecker.CheckResult;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;

public class Compat {
	public static class CompatMinecraft {
		private final Minecraft mc;

		public CompatMinecraft(final Minecraft mc) {
			this.mc = mc;
		}

		public Minecraft getMinecraftObj() {
			return this.mc;
		}

		public static @Nonnull CompatMinecraft getMinecraft() {
			return new CompatMinecraft(Minecraft.getInstance());
		}

		public @Nonnull CompatFontRenderer getFontRenderer() {
			return new CompatFontRenderer(this.mc.fontRenderer);
		}

		public @Nullable CompatSign.CompatWorld getWorld() {
			final World world = this.mc.world;
			if (world!=null)
				return new CompatSign.CompatWorld(world);
			return null;
		}

		public @Nullable CompatSign.CompatEntityPlayer getPlayer() {
			final ClientPlayerEntity player = this.mc.player;
			if (player!=null)
				return new CompatSign.CompatEntityPlayer(player);
			return null;
		}

		public @Nonnull CompatGameSettings getSettings() {
			return new CompatGameSettings(this.mc.gameSettings);
		}

		public @Nullable CompatSign.CompatNetHandlerPlayClient getConnection() {
			final ClientPlayNetHandler connection = this.mc.getConnection();
			return connection!=null ? new CompatSign.CompatNetHandlerPlayClient(connection) : null;
		}

		public TextureManager getTextureManager() {
			return this.mc.getTextureManager();
		}

		public File getGameDir() {
			return FMLPaths.GAMEDIR.get().toFile();
		}

		public boolean isGameFocused() {
			return this.mc.isGameFocused();
		}

		public CompatSession getSession() {
			return new CompatSession(this.mc.getSession());
		}
	}

	public static class CompatFontRenderer {
		private final FontRenderer font;

		public CompatFontRenderer(final FontRenderer font) {
			this.font = font;
		}

		public int drawString(final String msg, final float x, final float y, final int color, final boolean shadow) {
			return shadow ? this.font.drawStringWithShadow(msg, x, y, color) : this.font.drawString(msg, x, y, color);
		}

		public int drawString(final String msg, final float x, final float y, final int color) {
			return drawString(msg, x, y, color, false);
		}

		public int drawStringWithShadow(final String msg, final float x, final float y, final int color) {
			return drawString(msg, x, y, color, true);
		}

		public String wrapFormattedStringToWidth(final String msg, final int width) {
			return this.font.wrapFormattedStringToWidth(msg, width);
		}

		public int getStringWidth(final @Nullable String s) {
			return this.font.getStringWidth(s);
		}

		public int getStringWidthWithoutFormattingCodes(final @Nullable String s) {
			return getStringWidth(TextFormatting.getTextWithoutFormattingCodes(s));
		}

		public FontRenderer getFontRendererObj() {
			return this.font;
		}
	}

	public static class CompatGameSettings {
		private final GameSettings settings;

		public CompatGameSettings(final GameSettings settings) {
			this.settings = settings;
		}

		public GameSettings getSettingsObj() {
			return this.settings;
		}

		public int getAnisotropicFiltering() {
			return 0;
		}

		public String getLanguage() {
			return this.settings.language;
		}
	}

	public static class CompatSoundHandler {
		public static void playSound(final @Nonnull ResourceLocation location, final float volume) {
			CompatMinecraft.getMinecraft().getMinecraftObj().getSoundHandler().play(SimpleSound.master(new SoundEvent(location), volume));
		}
	}

	public static class CompatTextureUtil {
		public static final DynamicTexture missingTexture = MissingTextureSprite.getDynamicTexture();

		public static void processPixelValues(final int[] pixel, final int displayWidth, final int displayHeight) {
			throw new NotImplementedException("processPixelValues");
		}

		public static void allocateTextureImpl(final int id, final int miplevel, final int width, final int height, final float anisotropicFiltering) {
			TextureUtil.prepareImage(id, miplevel, width, height);
		}
	}

	public static class CompatMathHelper {
		public static int floor_float(final float value) {
			return MathHelper.floor(value);
		}

		public static int floor_double(final double value) {
			return MathHelper.floor(value);
		}
	}

	public static class CompatI18n {
		public static String format(final String format, final Object... args) {
			return I18n.format(format, args);
		}

		public static boolean hasKey(final String key) {
			return I18n.hasKey(key);
		}

		public static String translateToLocal(final String text) {
			return hasKey(text) ? format(text) : text;
		}
	}

	public static class CompatTexture {
		private final CompatSimpleTexture texture;

		public CompatTexture(final CompatSimpleTexture texture) {
			this.texture = texture;
		}

		public static CompatTexture getTexture(final CompatSimpleTexture texture) {
			return new CompatTexture(texture);
		}

		public CompatSimpleTexture getTextureObj() {
			return this.texture;
		}

		public void bindTexture() {
			this.texture.bindTexture();
		}

		public void uploadTexture(final InputStream image) throws IOException {
			this.texture.deleteGlTexture();
			try (
					NativeImage nativeimage = NativeImage.read(image);
			) {
				final boolean blur = true;
				final boolean clamp = false;

				TextureUtil.prepareImage(this.texture.getRawGlTextureId(), 0, nativeimage.getWidth(), nativeimage.getHeight());
				nativeimage.uploadTextureSub(0, 0, 0, 0, 0, nativeimage.getWidth(), nativeimage.getHeight(), blur, clamp, false);
			}
		}
	}

	public static class CompatResourceManager {
		private final IResourceManager manager;

		public CompatResourceManager(final IResourceManager manager) {
			this.manager = manager;
		}

		public IResourceManager getManagerObj() {
			return this.manager;
		}
	}

	public static class CompatSimpleTexture extends SimpleTexture {
		public CompatSimpleTexture(final ResourceLocation textureResourceLocation) {
			super(textureResourceLocation);
		}

		public int getRawGlTextureId() {
			return super.getGlTextureId();
		}

		@Override
		public void loadTexture(final IResourceManager manager) throws IOException {
			loadTexture(new CompatResourceManager(manager));
		}

		public void loadTexture(final CompatResourceManager manager) throws IOException {
			super.loadTexture(manager.getManagerObj());
		}
	}

	public enum CompatSide {
		COMMON,
		CLIENT,
		SERVER,
		;

		public ModConfig.Type toModConfigType() {
			switch (this) {
				case CLIENT:
					return ModConfig.Type.CLIENT;
				case SERVER:
					return ModConfig.Type.SERVER;
				default:
					return ModConfig.Type.COMMON;
			}
		}

		public static CompatSide fromModConfigType(final ModConfig.Type type) {
			switch (type) {
				case CLIENT:
					return CLIENT;
				case SERVER:
					return SERVER;
				default:
					return COMMON;
			}
		}
	}

	public static class CompatBufferBuilder {
		public BufferBuilder vbuilder;

		public CompatBufferBuilder(final BufferBuilder vbuilder) {
			this.vbuilder = vbuilder;
		}
	}

	public static abstract class CompatGlyph implements IGlyph {
		public final float width;
		public final float height;

		public CompatGlyph(final float width, final float height) {
			this.width = width;
			this.height = height;
		}

		@Override
		public float getAdvance() {
			return this.width;
		}

		@Override
		public float getBoldOffset() {
			return 0;
		}

		@Override
		public float getShadowOffset() {
			return 0;
		}
	}

	public static abstract class CompatTexturedGlyph extends TexturedGlyph {
		public CompatTexturedGlyph(final ResourceLocation texture, final float width, final float height) {
			super(texture, 0, 1, 0, 1, 0, width, 0+3, height+3);
		}

		public void onRender(final TextureManager textureManager, final boolean hasShadow, final float x, final float y, final CompatBufferBuilder vbuilder, final float red, final float green, final float blue, final float alpha) {
			super.render(textureManager, hasShadow, x, y, vbuilder.vbuilder, red, green, blue, alpha);
		}

		@Override
		public void render(final TextureManager textureManager, final boolean hasShadow, final float x, final float y, final BufferBuilder vbuilder, final float red, final float green, final float blue, final float alpha) {
			onRender(textureManager, hasShadow, x, y, new CompatBufferBuilder(vbuilder), red, green, blue, alpha);
		}
	}

	public static class CompatVersionChecker {
		public static void startVersionCheck(final String modId, final String modVersion, final String updateURL) {
		}

		public static CompatCheckResult getResult(final String modId) {
			final IModInfo container = ModList.get().getModContainerById(modId)
					.map(e -> e.getModInfo())
					.orElse(null);
			return CompatCheckResult.from(VersionChecker.getResult(container));
		}

		public static class CompatCheckResult {
			@Nonnull
			public final CompatStatus status;
			@Nullable
			public final String target;
			@Nullable
			public final Map<String, String> changes;
			@Nullable
			public final String url;

			public CompatCheckResult(@Nonnull final CompatStatus status, @Nullable final String target, @Nullable final Map<String, String> changes, @Nullable final String url) {
				this.status = status;
				this.target = target;
				this.changes = changes==null ? Collections.<String, String> emptyMap() : Collections.unmodifiableMap(changes);
				this.url = url;
			}

			public static CompatCheckResult from(final CheckResult result) {
				Map<String, String> compatChanges = null;
				if (result.changes!=null)
					compatChanges = result.changes.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue()));
				return new CompatCheckResult(CompatStatus.getStatus(result.status),
						result.target!=null ? result.target.toString() : null,
						compatChanges,
						result.url);
			}
		}

		public static enum CompatStatus {
			PENDING,
			FAILED,
			UP_TO_DATE,
			OUTDATED,
			AHEAD,
			BETA,
			BETA_OUTDATED,
			;

			public static CompatStatus getStatus(final VersionChecker.Status status) {
				switch (status) {
					default:
					case PENDING:
						return CompatStatus.PENDING;
					case FAILED:
						return CompatStatus.FAILED;
					case UP_TO_DATE:
						return CompatStatus.UP_TO_DATE;
					case OUTDATED:
						return CompatStatus.OUTDATED;
					case AHEAD:
						return CompatStatus.AHEAD;
					case BETA:
						return CompatStatus.BETA;
					case BETA_OUTDATED:
						return CompatStatus.BETA_OUTDATED;
				}
			}
		}
	}

	public static class CompatSession {
		private final Session session;

		public CompatSession(final Session session) {
			this.session = session;
		}

		public String getPlayerID() {
			return this.session.getPlayerID();
		}

		public String getUsername() {
			return this.session.getUsername();
		}

		public String getToken() {
			return this.session.getToken();
		}
	}

	public static class CompatMinecraftVersion {
		public static String getMinecraftVersion() {
			return MCPVersion.getMCVersion();
		}

		public static String getForgeVersion() {
			return ForgeVersion.getVersion();
		}
	}
}
