package com.example.qtor.constant

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import com.example.qtor.R
import com.example.qtor.data.model.Tool

const val IMAGE_TO_EDIT = "image_to_edit"
const val TYPE_ALL_IMAGE = "image/*"
val LIST_OF_TOOLS = mutableListOf(
    "TEMPLATE",
    "TOOLS",
    "PORTRAIT",
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
const val STORAGE_FONTS = "fonts"
const val ASSET_PATH = "file:///android_asset/"
const val BASE_URL = "https://apis.clipdrop.co/"
const val OLD_EXTENSION = "webp"
const val NEW_EXTENSION = "png"
const val FILTER_ALPHA = 0.3f
const val ONE_SECOND = 1000L
const val TOOL_INIT_INDEX = "ToolActive"
const val ADJUST_TOOL = 4
val ADJUST_LIST_TOOLS = listOf(
    Tool(R.drawable.ic_brightness, R.string.adjust_brightness),
    Tool(R.drawable.ic_warp, R.string.adjust_warmth),
    Tool(R.drawable.ic_contrast, R.string.adjust_contrast),
    Tool(R.drawable.ic_saturation, R.string.adjust_saturation),
//    Tool(R.drawable.ic_saturation, R.string.adjust_vibrance),
//    Tool(R.drawable.ic_sharpen, R.string.adjust_sharpen),
//    Tool(R.drawable.ic_grain, R.string.adjust_Grain),
//    Tool(R.drawable.ic_hightlight, R.string.adjust_highlight),
//    Tool(R.drawable.ic_shadow, R.string.adjust_shadow),
//    Tool(R.drawable.ic_fade, R.string.adjust_fade),
//    Tool(R.drawable.ic_brilliance, R.string.adjust_brilliance),
//    Tool(R.drawable.ic_tint, R.string.adjust_Tint)
)
const val FIFTH_TI = 50
const val HUREND = 100
const val ADJUST_BRIGHTNESS=0
const val ADJUST_WARMTH=1
const val ADJUST_CONTRAST=2
const val ADJUST_SATURATION=3
const val URI_SAVED_IMAGE="uri"
val tools = listOf(
    Tool(R.drawable.ic_template, R.string.tool_template),
    Tool(R.drawable.ic_remove_object, R.string.tool_remove_object),
    Tool(R.drawable.ic_portrait, R.string.tool_portrait),
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