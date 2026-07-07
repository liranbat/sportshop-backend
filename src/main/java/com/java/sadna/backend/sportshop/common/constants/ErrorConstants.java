package com.java.sadna.backend.sportshop.common.constants;

public final class ErrorConstants {

    public static final class Product {
        public static final String NOT_FOUND = "product.notFound";
        public static final String VERSION_MISMATCH = "product.versionMismatch";
        public static final String INVALID_IMAGE_URL = "product.invalidImageUrl";
        public static final String STOCK_ENTRY_REQUIRED = "product.stock.entryRequired";
        public static final String STOCK_SIZE_TOKEN_INVALID = "product.stock.sizeTokenInvalid";
        public static final String STOCK_ENTRY_MISSING = "product.stock.entryMissing";
        public static final String STOCK_QTY_NON_NEGATIVE = "product.stock.qtyNonNegative";
        public static final String STOCK_THRESHOLD_NON_NEGATIVE = "product.stock.thresholdNonNegative";
        public static final String STOCK_MULTI_SIZE_CANNOT_BE_ONE = "product.stock.multiSizeCannotBeOne";
        public static final String STOCK_SINGLE_SIZE_MUST_BE_ONE = "product.stock.singleSizeMustBeOne";

        private Product() {}
    }

    public static final class Stock {
        public static final String DELTA_NON_ZERO = "stock.deltaNonZero";
        public static final String ADJUST_CONFLICT = "stock.adjustConflict";
        public static final String SIZE_BLANK = "stock.sizeBlank";
        public static final String ONE_SIZE_RESERVED = "stock.oneSizeReserved";
        public static final String MULTI_SIZE_CANNOT_ADD = "stock.multiSize.cannotAdd";
        public static final String ONE_SIZE_NOT_REMOVABLE = "stock.oneSizeNotRemovable";
        public static final String MULTI_SIZE_CANNOT_REMOVE = "stock.multiSize.cannotRemove";
        public static final String ROW_NOT_FOUND = "stock.rowNotFound";
        public static final String SIZE_ALREADY_EXISTS = "stock.sizeAlreadyExists";

        private Stock() {}
    }

    public static final class Auth {
        public static final String EMAIL_TAKEN = "auth.emailTaken";
        public static final String INVALID_CREDENTIALS = "auth.invalidCredentials";
        public static final String INVALID_REFRESH = "auth.invalidRefresh";
        public static final String SESSION_INVALID = "auth.session.invalidSession";
        public static final String SESSION_REVOKE_CONFLICT = "auth.session.revokeConflict";
        public static final String SESSION_SCOPE_MUST_BE_OTHERS = "auth.session.scopeMustBeOthers";

        private Auth() {}
    }

    public static final class User {
        public static final String NOT_FOUND = "user.notFound";
        public static final String NOT_ACTIVE = "user.notActive";
        public static final String CURRENT_PASSWORD_INCORRECT = "user.currentPasswordIncorrect";
        public static final String CONCURRENT_MODIFICATION = "user.concurrentModification";
        public static final String DELETE_CONFLICT = "user.deleteConflict";
        public static final String PROMOTE_CONFLICT = "user.promoteConflict";
        public static final String DEMOTE_CONFLICT = "user.demoteConflict";
        public static final String ADMIN_LAST_ADMIN_DELETE = "user.admin.lastAdminDelete";
        public static final String ADMIN_SOFT_DELETE_CONFLICT = "user.admin.softDeleteConflict";
        public static final String ADMIN_RESTORE_CONFLICT = "user.admin.restoreConflict";

        private User() {}
    }

    public static final class Order {
        public static final String NOT_FOUND = "order.notFound";
        public static final String CANNOT_BE_CANCELLED = "order.cannotBeCancelled";
        public static final String INVALID_STATUS_TRANSITION = "order.invalidStatusTransition";
        public static final String STATUS_CHANGED = "order.statusChanged";
        public static final String SHIPPING_NOT_EDITABLE = "order.shippingNotEditable";

        private Order() {}
    }

    public static final class Cart {
        public static final String QTY_MIN_ADD = "cart.qtyMinAdd";
        public static final String PRODUCT_UNAVAILABLE = "cart.productUnavailable";
        public static final String SIZE_UNAVAILABLE = "cart.sizeUnavailable";
        public static final String INSUFFICIENT_STOCK = "cart.insufficientStock";
        public static final String ADD_FAILED = "cart.addFailed";
        public static final String QTY_MIN_PATCH = "cart.qtyMinPatch";
        public static final String ITEM_NOT_FOUND = "cart.itemNotFound";
        public static final String EMPTY = "cart.empty";

        private Cart() {}
    }

    public static final class Category {
        public static final String NOT_FOUND = "category.notFound";
        public static final String REPLACEMENT_SAME_AS_TARGET = "category.replacementSameAsTarget";
        public static final String REPLACEMENT_NOT_ACTIVE = "category.replacementNotActive";
        public static final String ALREADY_DELETED = "category.alreadyDeleted";
        public static final String NOT_DELETED = "category.notDeleted";
        public static final String INVALID_ID = "category.invalidId";
        public static final String INVALID_ICON_URL = "category.invalidIconUrl";

        private Category() {}
    }

    public static final class Payment {
        public static final String DECLINED = "payment.declined";

        private Payment() {}
    }

    public static final class Image {
        public static final String NOT_IMAGE = "image.notImage";
        public static final String UNSUPPORTED_TYPE = "image.unsupportedType";
        public static final String TOO_LARGE = "image.tooLarge";
        public static final String SVG_UNSAFE = "image.svgUnsafe";
        public static final String MULTIPART_FILE_REQUIRED = "image.multipartFileRequired";
        public static final String EMPTY = "image.empty";
        public static final String EXTENSION_REQUIRED = "image.extensionRequired";
        public static final String READ_FAILED = "image.readFailed";
        public static final String STORE_FAILED = "image.storeFailed";
        public static final String PATH_ESCAPE = "image.pathEscape";
        public static final String KEY_EXHAUSTED = "image.keyExhausted";

        private Image() {}
    }

    public static final class Checkout {
        public static final String VERSION_MISMATCH = "checkout.versionMismatch";
        public static final String INSUFFICIENT_STOCK_PREFLIGHT = "checkout.insufficientStock.preflight";
        public static final String INSUFFICIENT_STOCK_RACE = "checkout.insufficientStock.race";
        public static final String ORDER_NUMBER_EXHAUSTED = "checkout.orderNumberExhausted";

        private Checkout() {}
    }

    public static final class Http {
        public static final String UNAUTHORIZED = "http.unauthorized";
        public static final String FORBIDDEN = "http.forbidden";
        public static final String METHOD_NOT_ALLOWED = "http.methodNotAllowed";
        public static final String NOT_FOUND_DEFAULT = "http.notFound.default";
        public static final String INTERNAL_ERROR = "http.internalError";
        public static final String CONFLICT_DEFAULT = "http.conflict.default";
        public static final String BAD_GATEWAY_DEFAULT = "http.badGateway.default";

        public static final String BAD_REQUEST_DEFAULT = "http.badRequest.default";
        public static final String BAD_REQUEST_TYPE_MISMATCH = "http.badRequest.typeMismatch";
        public static final String BAD_REQUEST_VALIDATION_FAILED = "http.badRequest.validationFailed";
        public static final String BAD_REQUEST_CONSTRAINT_FAILED = "http.badRequest.constraintFailed";
        public static final String BAD_REQUEST_MISSING_PARAM = "http.badRequest.missingParam";
        public static final String BAD_REQUEST_MALFORMED_BODY = "http.badRequest.malformedBody";
        public static final String BAD_REQUEST_SORT_FIELD_REQUIRED = "http.badRequest.sortFieldRequired";
        public static final String BAD_REQUEST_UNKNOWN_SORT_FIELD = "http.badRequest.unknownSortField";
        public static final String BAD_REQUEST_INVALID_SORT_DIRECTION = "http.badRequest.invalidSortDirection";

        public static final String PAYLOAD_TOO_LARGE_DEFAULT = "http.payloadTooLarge.default";
        public static final String PAYLOAD_TOO_LARGE_UPLOAD = "http.payloadTooLarge.upload";

        public static final String MEDIA_TYPE_UNSUPPORTED = "http.mediaType.unsupported";
        public static final String MEDIA_TYPE_MISSING = "http.mediaType.missing";

        private Http() {}
    }

    public static final class Pagination {
        public static final String PAGE_NEGATIVE = "pagination.pageNegative";
        public static final String PAGE_SIZE_MIN = "pagination.pageSizeMin";
        public static final String OUT_OF_RANGE = "pagination.outOfRange";

        private Pagination() {}
    }

    private ErrorConstants() {}
}
