package com.example.qtor.constant

import androidx.compose.ui.graphics.Color
import com.example.qtor.R
import com.example.qtor.data.model.FilterObj
import com.example.qtor.data.model.Language
import com.example.qtor.data.model.Tool

const val IMAGE_TO_EDIT = "image_to_edit"
const val TYPE_ALL_IMAGE = "image/*"
val LIST_OF_TOOLS = mutableListOf(
    "TEMPLATE",
    "TOOLS",
    "FRAME",
    "FILTERS",
    "ADJUST",
    "EFFECTS",
    "STICKERS",
    "TEXT",
    "TIMESTAMP",
    "DRAW SHAPE",
    "ADD PHOTOS",
    "AI FILTER"
)
const val EDIT_IMAGE_TOOl = 1
const val FRAME_TOOL = 2
const val STICKER_TOOL = 6
const val TEXT_TOOL = 7
const val FILTERS_TOOl = 3
const val STORAGE_STICKERS = "stickers"
const val ZERO = 0f
const val ITEM_ACTIVE_NULL = -1
const val RECT_ITEM_EDIT_SIZE = 40
const val FIRST_INDEX = 0
const val DETECT_OBJECT_MODE = 0
const val BRUSH_MODE = 1
const val LASSO_MODE = 2
const val LOCAL_MODEL_FILE_PATH = "custom_models/object_labeler.tflite"
const val LABEL_PERSON = "Person"
const val TYPE_PEOPLE = 0
const val TYPE_OTHERS = 1
const val MAIN_TOOl_REMOVE_OBJECT = 1
const val MAIN_TOOL_STICKERS = 6
const val MAIN_TOOL_TEXT = 7
const val IMG_QUALITY = 100
const val IMAGE_FILE = "image_file"
const val MASK_FILE = "mask_file"
const val FILE_NAME = "image"
const val FILE_NAME_MASK = "mask.png"
const val MEDIA_PARSE_TYPE = "image/*"
const val HEADER_AUTH_KEY = "x-api-key"
const val IN_PAINTING_RADIUS = 5.0
const val DRAW_ALPHA = 0.6f
const val LOADING = false
const val INDILE = true
const val STORAGE_FILTERS = "filters"
const val STORAGE_FRAMES = "frames"
const val STORAGE_FONTS = "fonts"
const val ASSET_PATH = "file:///android_asset/"
const val BASE_URL = "https://apis.clipdrop.co/"
const val OLD_EXTENSION = "webp"
const val NEW_EXTENSION = "png"
const val FILTER_ALPHA = 0.2f
const val ONE_SECOND = 1000L
const val TOOL_INIT_INDEX = "ToolActive"
const val ADJUST_TOOL = 4
val ADJUST_LIST_TOOLS = listOf(
    Tool(R.drawable.ic_brightness, R.string.adjust_brightness),
    Tool(R.drawable.ic_warp, R.string.adjust_warmth),
    Tool(R.drawable.ic_contrast, R.string.adjust_contrast),
    Tool(R.drawable.ic_saturation, R.string.adjust_saturation),
    Tool(R.drawable.ic_saturation, R.string.adjust_vibrance),
    Tool(R.drawable.ic_sharpen, R.string.adjust_sharpen),
    Tool(R.drawable.ic_grain, R.string.adjust_Grain),
    Tool(R.drawable.ic_hightlight, R.string.adjust_highlight),
    Tool(R.drawable.ic_shadow, R.string.adjust_shadow),
    Tool(R.drawable.ic_fade, R.string.adjust_fade),
    Tool(R.drawable.ic_brilliance, R.string.adjust_brilliance),
    Tool(R.drawable.ic_tint, R.string.adjust_Tint)
)
const val FIFTH_TI = 50
const val HUREND = 100
const val ADJUST_BRIGHTNESS = 0
const val ADJUST_WARMTH = 1
const val ADJUST_CONTRAST = 2
const val ADJUST_SATURATION = 3
const val URI_SAVED_IMAGE = "uri"
val tools = listOf(
    Tool(R.drawable.ic_template, R.string.tool_template),
    Tool(R.drawable.ic_remove_object, R.string.tool_remove_object),
    Tool(R.drawable.ic_frame, R.string.tool_frame),
    Tool(R.drawable.ic_filter, R.string.tool_filters),
    Tool(R.drawable.ic_adjust, R.string.tool_Adjust),
    Tool(R.drawable.ic_effects, R.string.tool_effects),
    Tool(R.drawable.ic_template, R.string.tool_stickers),
    Tool(R.drawable.ic_remove_object, R.string.tool_text)
)
val images = listOf(
    R.drawable.demo1,
    R.drawable.demo2,
    R.drawable.demo3,
    R.drawable.demo4,
    R.drawable.demo5,
    R.drawable.demo6,
)
const val OWNER_FACEBOOK = "https://www.facebook.com/quangthang46/"
const val OWNER_INSTAGRAM = "https://www.instagram.com/pqt46/"
const val OWNER_TIKTOK = "https://www.tiktok.com/@qthang46"
const val OWNER_GMAIL = "pqt4621@gmail.com"
const val MAIL_TO = "mailto:"
const val MAIL_SUBJECT = "QTOR Support"
const val INTENT_TITLE = "Send email"
const val DEFAULT_MESSAGE_VALUE = "   "
val LANGUAGES = listOf(
    Language(R.string.us, "en", R.drawable.ic_us),
    Language(R.string.vn, "vi", R.drawable.ic_vn),
)
const val QTOR_SHARED = "qtor"
const val LANGUAGE_SHARED = "language"
val colors = mutableListOf(
    Color(0xffffffff), Color(0xffbbbbbb), Color(0xff4e4e4e),
    Color(0xFF212121), Color(0xFF010101), Color(0xffffd7cd),
    Color(0xfffaab9c), Color(0xffcd3041), Color(0xfffff3c3),
    Color(0xfffee46b), Color(0xfff5af57), Color(0xfff48432),
    Color(0xfff43d03), Color(0xfffff1f4), Color(0xfffee1e3),
    Color(0xfff7aab4), Color(0xfffc2777), Color(0xffebd4e8),
    Color(0xffba63b2), Color(0xffa8368e), Color(0xff93d3f9),
    Color(0xff80aced), Color(0xff2260a9), Color(0xff93d3f9),
    Color(0xffdeefe9), Color(0xffb0d0c3), Color(0xff4aaea2),
    Color(0xff0e8c77), Color(0xff05674e), Color(0xffd2e5a3),
    Color(0xffaecd7d), Color(0xffa2ae1a), Color(0xff6e8618),
    Color(0xff446240), Color(0xffe4d8be), Color(0xffd5c68f),
    Color(0xffa58258), Color(0xff74462c)
)
const val API_KEY =
    "6e5026a54275eee7d07f3c8f2742c8cec7da474d2b3f34432f678c421526d39d7856f28e1c7142f775cfd9fa92cada45"
const val GOOGLE_URL = "https://www.google.com/"
const val APP_NAME = "qtor"
const val FRAME_FOLDER = "frames"
val FRAME_TITLES = listOf<Int>(
    R.string.frame_1,
    R.string.frame_2,
    R.string.frame_3,
    R.string.frame_4,
    R.string.frame_5,
    R.string.frame_6,
    R.string.frame_7
)
val Filters = listOf(
    FilterObj(
        saturation = 1f,
        brightness = 1.01f,
        warmth = 1f,
        contrast = 1f,
        nameID = R.string.filter_none
    ),
    FilterObj(
        saturation = 1f,
        brightness = 1.01f,
        warmth = 0.5f,
        contrast = 1f,
        nameID = R.string.filter_vivid
    ),
    FilterObj(
        saturation = 1.5f,
        brightness = 1.05f,
        warmth = 0.96f,
        contrast = 0.98f,
        nameID = R.string.filter_playa
    ),
    FilterObj(
        saturation = 0.17863534f,
        brightness = 1.0995761f,
        warmth = 1.4455452f,
        contrast = 1.0975568f,
        nameID = R.string.filter_honey
    ),
    FilterObj(
        saturation = 1.5727823f,
        brightness = 0.94126517f,
        warmth = 0.86413074f,
        contrast = 1.0673418f,
        nameID = R.string.filter_isla
    ),
    FilterObj(
        saturation = 1.3461764f,
        brightness = 0.9895106f,
        warmth = 1.0229769f,
        contrast = 1.0500408f,
        nameID = R.string.filter_desert
    ),
    FilterObj(
        saturation = 1.5031081f,
        brightness = 0.89285934f,
        warmth = 0.9459637f,
        contrast = 1.1156896f,
        nameID = R.string.filter_clay
    ),
    FilterObj(
        saturation = 1.2f,
        brightness = 1.1f,
        warmth = 1.1f,
        contrast = 1.2f,
        nameID = R.string.filter_palma
    ),
    FilterObj(
        saturation = 0.9f,
        brightness = 0.9f,
        warmth = 0.9f,
        contrast = 1.2f,
        nameID = R.string.filter_blush
    ),
    FilterObj(
        saturation = 0.9f,
        brightness = 0.9f,
        warmth = 0.8f,
        contrast = 1.3f,
        nameID = R.string.filter_alpaca
    )
)
const val BITMAP_MAX_PIXEL_CLOUD = 12000000
const val MAIN_TOOL_FILTER = 3