function customCheckbox() {
    var checkBox = $('input[type="checkbox"]');
    $(checkBox).each(function () {
        if (!$(this).parent().hasClass("custom-checkbox")) {
            $(this).wrap("<span class='custom-checkbox'></span>");
            if ($(this).is(':checked')) {
                $(this).parent().addClass("selected");
            }
            $(this).click(function () {
                $(this).parent().toggleClass("selected");
            });
        }
    });
}

function optimizePageByResolution() {
    var page = $("html");
    var currentZoom = page.css("zoom");
    var width = $(window).width() * currentZoom;
    var zoom = -2.49082 * Math.pow(10, -7) * Math.pow(width, 2) + 0.000931996 * width - 0.0849259;
    page.css("zoom", zoom);
}

function bindOptimizers() {
    optimizePageByResolution();
    $(window).resize(function () {
        optimizePageByResolution();
    });
}

$(document).ready(function () {
    bindOptimizers();
    customCheckbox();
});
