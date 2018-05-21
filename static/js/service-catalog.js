/**
 * Created by trofiv on 19.03.2015.
 */
function bindPoolClick() {

    function clearHint(timeout) {
        hideProcessingTable();
        $.each(timeout, function (index, item) {
            clearTimeout(item);
        });
        $("#provisioning-button").button("option", {
            disabled: false
        });
        $("#capacity").val("10");
    }

    function bindBlurCapacity(capacity) {
        capacity.on("blur", function () {
            if (capacity.val().length === 0) {
                capacity.val("1");
            }
        });
    }

    function bindKeypressCapacity(capacity, provisionButton) {
        capacity.on("keypress", function (e) {
            var key = e.keyCode ? e.keyCode : e.which;
            //noinspection JSCheckFunctionSignatures
            if (isNaN(String.fromCharCode(key))) {
                return false;
            }
            $.each(timeout, function (index, item) {
                clearTimeout(item);
            });
            hideProcessingTable();
            resetProcessing();
            provisionButton.button("option", {
                disabled: false
            });
        });
    }

    function resetProcessing() {
        $("#capacity").prop('disabled', false);
        $("#provisioning-button").html('<span class="ui-button-text">Provision LUN</span>')
    }

    function setProvisioningProcessing() {
        $("#provisioning-button").html('<img class="processing" src="/images/loading.gif">')
    }

    function setProvisioningDone() {
        $("#provisioning-button").html('<img class="done" src="/images/ok.png">')

    }

    function setProvisioningFail() {
        $("#provisioning-button").html('<img class="fail" src="/images/error.png">')
    }

    function hideProcessingTable() {
        $("#provisioning-button").html('<span class="ui-button-text">Provision LUN</span>')
    }

    function showProcessingTable() {
        $("#provisioning-button").html('<img class="processing" src="/images/loading.gif">')
    }


    function processClickOnPool(hint, item, zoom, e, pointerWrapper, pointer, timeout) {
        updateHintInfo(hint, item);
        clearHint(timeout);
        resetProcessing();
        showHintInNewPlace(zoom, e, hint, pointerWrapper, pointer);
    }

    function processClickOnSamePool(hint, zoom, e, pointerWrapper, pointer) {
        hint.hide("blind", {direction: openDirection}, 200, function () {
            pointerWrapper.hide("blind", {direction: openDirection}, 100, function () {
                showHintInNewPlace(zoom, e, hint, pointerWrapper, pointer);
            });
        });
    }

    function processClickOnDifferentPool(hint, item, zoom, e, pointerWrapper, pointer, timeout) {
        hint.removeAttr("vp_id");
        hint.hide("blind", {direction: openDirection}, 200, function () {
            pointerWrapper.hide("blind", {direction: openDirection}, 100, function () {
                updateHintInfo(hint, item);
                clearHint(timeout);
                resetProcessing();
                showHintInNewPlace(zoom, e, hint, pointerWrapper, pointer);
            });
        });
    }

    function processClickOnUnknownSpace(hint, pointerWrapper) {
        hint.removeAttr("vp_id");
        clearHint(timeout);
        resetProcessing();
        hint.hide("blind", {direction: openDirection}, 200, function () {
            pointerWrapper.hide("blind", {direction: openDirection}, 100);
        });
    }

    function showHintInNewPlace(zoom, e, hint, pointerWrapper, pointer) {
        var parent = $("#content-area-wrapper");
        var parentWidth = parent.width();
        var parentHeight = parent.height();
        var width = hint.width();
        var height = hint.height();
        var pointerSize = 18;
        var mousePosition = {
            left: e.pageX / zoom,
            top: e.pageY / zoom
        };
        var offset = getRelativePosition(mousePosition, parent);
        if (offset.top <= height + pointerSize) {
            if (offset.left <= width / 2) {
                showLeftTopHint(offset, hint, pointerWrapper, pointer);
            } else if (offset.left + width / 2 >= parentWidth) {
                showRightTopHint(offset, hint, pointerWrapper, pointer);
            } else {
                showTopCenterHint(offset, hint, pointerWrapper, pointer);
            }
        } else if (offset.left <= width / 2) {
            if (offset.top + height / 2 >= parentHeight) {
                showLeftBottomHint(offset, hint, pointerWrapper, pointer);
            } else {
                showLeftCenterHint(offset, hint, pointerWrapper, pointer);
            }
        } else if (offset.left + width / 2 >= parentWidth) {
            if (offset.top + height / 2 >= parentHeight) {
                showRightBottomHint(offset, hint, pointerWrapper, pointer);
            } else {
                showRightCenterHint(offset, hint, pointerWrapper, pointer);
            }
        } else {
            showBottomCenterHint(offset, hint, pointerWrapper, pointer);
        }
    }

    function updateHintInfo(hint, item) {
        hint.attr("vp_id", $(item).attr("vp_id"));
        hint.attr("node_id", $(item).attr("node_id"));
        hint.find(".header").find(".name").text($(item).attr("vp_name"));
        hint.find(".content").find(".latency").text($(item).attr("latency"));
    }

    function eventIsExecuteOnBlock(eventBlock, event) {
        return $(eventBlock).is(event.target) || !($(eventBlock).has(event.target).length === 0);
    }

    function getRelativePosition(elementPosition, parent) {
        var position = {};
        var parentOffset = parent.offset();
        position["top"] = elementPosition.top - parentOffset.top;
        position["left"] = elementPosition.left - parentOffset.left;
        return position;
    }

    function showBottomCenterHint(offset, hint, pointerWrapper, pointer) {
        openDirection = "down";
        pointerWrapper.attr("class", "hint-pointer-wrapper").addClass("bottom");
        pointer.attr("class", "hint-pointer").addClass("bottom");
        pointerWrapper.css({
            top: (offset.top - pointerWrapper.height() + 2) + "px",
            left: (offset.left - pointerWrapper.width() / 2) + "px"
        });
        hint.css({
            top: (offset.top - hint.height() - pointerWrapper.height() + 1) + "px",
            left: (offset.left - hint.width() / 2) + "px"
        });
        pointerWrapper.clearQueue().show("blind", {direction: openDirection}, 100, function () {
            hint.clearQueue().show("blind", {direction: openDirection}, 200);
        });
    }

    function showRightCenterHint(offset, hint, pointerWrapper, pointer) {
        openDirection = "right";
        pointerWrapper.attr("class", "hint-pointer-wrapper").addClass("right");
        pointer.attr("class", "hint-pointer").addClass("right");
        pointerWrapper.css({
            top: (offset.top - pointerWrapper.height() / 2) + "px",
            left: (offset.left - pointerWrapper.width() + 1) + "px"
        });
        hint.css({
            top: (offset.top - hint.height() / 2) + "px",
            left: (offset.left - hint.width() - pointerWrapper.width()) + "px"
        });
        pointerWrapper.clearQueue().show("blind", {direction: openDirection}, 100, function () {
            hint.clearQueue().show("blind", {direction: openDirection}, 200);
        });
    }

    function showRightBottomHint(offset, hint, pointerWrapper, pointer) {
        openDirection = "right";
        pointerWrapper.attr("class", "hint-pointer-wrapper").addClass("right");
        pointer.attr("class", "hint-pointer").addClass("right");
        pointerWrapper.css({
            top: (offset.top - pointerWrapper.height() / 2) + "px",
            left: (offset.left - pointerWrapper.width()) + "px"
        });
        hint.css({
            top: (offset.top - hint.height() + pointerWrapper.height()) + "px",
            left: (offset.left - hint.width() - pointerWrapper.width()) + "px"
        });
        pointerWrapper.clearQueue().show("blind", {direction: openDirection}, 100, function () {
            hint.clearQueue().show("blind", {direction: openDirection}, 200);
        });
    }

    function showRightTopHint(offset, hint, pointerWrapper, pointer) {
        openDirection = "right";
        pointerWrapper.attr("class", "hint-pointer-wrapper").addClass("right");
        pointer.attr("class", "hint-pointer").addClass("right");
        pointerWrapper.css({
            top: (offset.top - pointerWrapper.height() / 2) + "px",
            left: (offset.left - pointerWrapper.width()) + "px"
        });
        hint.css({
            top: (offset.top - pointerWrapper.height()) + "px",
            left: (offset.left - hint.width() - pointerWrapper.width()) + "px"
        });
        pointerWrapper.clearQueue().show("blind", {direction: openDirection}, 100, function () {
            hint.clearQueue().show("blind", {direction: openDirection}, 200);
        });
    }

    function showLeftCenterHint(offset, hint, pointerWrapper, pointer) {
        openDirection = "left";
        pointerWrapper.attr("class", "hint-pointer-wrapper").addClass("left");
        pointer.attr("class", "hint-pointer").addClass("left");
        pointerWrapper.css({
            top: (offset.top - pointerWrapper.height() / 2) + "px",
            left: offset.left + "px"
        });
        hint.css({
            top: (offset.top - hint.height() / 2) + "px",
            left: (offset.left + pointerWrapper.width() - 4) + "px"
        });
        pointerWrapper.clearQueue().show("blind", {direction: openDirection}, 100, function () {
            hint.clearQueue().show("blind", {direction: openDirection}, 200);
        });
    }

    function showLeftBottomHint(offset, hint, pointerWrapper, pointer) {
        openDirection = "left";
        pointerWrapper.attr("class", "hint-pointer-wrapper").addClass("left");
        pointer.attr("class", "hint-pointer").addClass("left");
        pointerWrapper.css({
            top: (offset.top - pointerWrapper.height() / 2) + "px",
            left: offset.left + "px"
        });
        hint.css({
            top: (offset.top - hint.height() + pointerWrapper.height() / 2) + "px",
            left: (offset.left + pointerWrapper.width() - 4) + "px"
        });
        pointerWrapper.clearQueue().show("blind", {direction: openDirection}, 100, function () {
            hint.clearQueue().show("blind", {direction: openDirection}, 200);
        });
    }

    function showLeftTopHint(offset, hint, pointerWrapper, pointer) {
        openDirection = "left";
        pointerWrapper.attr("class", "hint-pointer-wrapper").addClass("left");
        pointer.attr("class", "hint-pointer").addClass("left");
        pointerWrapper.css({
            top: (offset.top - pointerWrapper.height() / 2) + "px",
            left: offset.left + "px"
        });
        hint.css({
            top: (offset.top - pointerWrapper.height()) + "px",
            left: (offset.left + pointerWrapper.width() - 3) + "px"
        });
        pointerWrapper.clearQueue().show("blind", {direction: openDirection}, 100, function () {
            hint.clearQueue().show("blind", {direction: openDirection}, 200);
        });
    }

    function showTopCenterHint(offset, hint, pointerWrapper, pointer) {
        openDirection = "up";
        pointerWrapper.attr("class", "hint-pointer-wrapper").addClass("top");
        pointer.attr("class", "hint-pointer").addClass("top");
        pointerWrapper.css({
            top: offset.top + "px",
            left: (offset.left - pointerWrapper.width() / 2) + "px"
        });
        hint.css({
            top: (offset.top + pointerWrapper.height() - 3) + "px",
            left: (offset.left - hint.width() / 2) + "px"
        });
        pointerWrapper.clearQueue().show("blind", {direction: openDirection}, 100, function () {
            hint.clearQueue().show("blind", {direction: openDirection}, 200);
        });
    }

    function bindCloseHintButtonClick(hint, pointerWrapper) {
        $("#hint-close-button").click(function () {
            processClickOnUnknownSpace(hint, pointerWrapper);
        });
    }

    function bindProvisionButtonClick(provisionButton, capacity) {
        provisionButton.button().click(function () {
            showProcessingTable();
            doProvisioning(provisionButton, capacity);
        }).button("option", {
            disabled: true
        });
    }

    function bindHintButtons(hint, pointerWrapper) {
        var provisionButton = $("#provisioning-button");
        var capacity = $("#capacity");
        bindProvisionButtonClick(provisionButton, capacity);
        bindCloseHintButtonClick(hint, pointerWrapper);
        bindKeypressCapacity(capacity, provisionButton);
        bindBlurCapacity(capacity);
    }

    function doProvisioning(provisionButton, capacity) {
        var data = {};
        var hint = $('.hint');
        data['name'] = 'yep';
        data['virtualPoolId'] = hint.attr('vp_id');
        data['nodeId'] = hint.attr('node_id');
        data['capacity'] = 10;
        $.ajax({
            contentType: "application/json; charset=utf-8",
            url: "/service-catalog/provision",
            type: "POST",
            data: JSON.stringify(data),
            beforeSend: function () {
                capacity.prop('disabled', true);
                provisionButton.button("option", {
                    disabled: true
                });
                setProvisioningProcessing();
            }
        }).done(function () {
            capacity.prop('disabled', false);
            provisionButton.button("option", {
                disabled: false
            });
            setProvisioningDone();
        }).fail(function () {
            capacity.prop('disabled', false);
            provisionButton.button("option", {
                disabled: false
            });
            setProvisioningFail();
        });
    }

    var zoom = $("html").css("zoom");
    var associatedPoolPanel = null;
    var openDirection = null;
    var timeout = [];
    var hint = $(".hint");
    var pointerWrapper = $(".hint-pointer-wrapper");
    var pointer = $(".hint-pointer");
    bindHintButtons(hint, pointerWrapper);
    $(document).mousedown(function (e) {
        var handlerFound = false;
        if (!eventIsExecuteOnBlock(hint, e) && !eventIsExecuteOnBlock(pointer, e) && !eventIsExecuteOnBlock(pointerWrapper, e)) {
            $.each($(".catalog-item"), function (index, item) {
                if (eventIsExecuteOnBlock(this, e)) {
                    handlerFound = true;
                    if (associatedPoolPanel == null) {
                        processClickOnPool(hint, item, zoom, e, pointerWrapper, pointer, timeout);
                    } else if (associatedPoolPanel !== this) {
                        processClickOnDifferentPool(hint, item, zoom, e, pointerWrapper, pointer, timeout);
                    } else {
                        processClickOnSamePool(hint, zoom, e, pointerWrapper, pointer);
                    }
                    associatedPoolPanel = this;
                    return false;
                }
            });
            if (!handlerFound) {
                associatedPoolPanel = null;
                processClickOnUnknownSpace(hint, pointerWrapper);
            }
        }
    });
}


function reloadServiceCatalogs() {

    function toggleLoadingAnimation() {
        $("#content-area-hider").toggle();
        $("#catalog").toggleClass("blur");
    }

    function clearCatalogs() {
        $("#catalog").empty();
    }

    function addCatalog(id, node, vp_name, latency, type) {
        latency = (latency == null) ? "Not defined" : (latency + " ms");
        type = (type == null) ? "Not defined" : type;
        $("#catalog").append('<div class="catalog-item" vp_id="' + id + '" ' +
            'vp_name="' + vp_name + '" ' +
            'node_id="' + node + '" ' +
            'latency="' + latency + '"' + '>' +
            '<div class="panel">' +
            '<div class="panel-heading">' +
            '<h3 class="panel-title">' + vp_name + '</h3>' +
            '</div>' +
            '<div class="panel-body">' +
            '<div class="catalog-image">' +
            '<div class="catalog-image-inner">' +
            '<img class="" src="/images/service-pool.png"/>' +
            '</div>' +
            '</div>' +
            '<p class="catalog-description">Node: ' + node + '</p>' +
            '<p class="catalog-description">Latency: ' + latency + '</p>' +
            '<p class="catalog-description">Workload type: ' + type + '</p>' +
            '</div>' +
            '</div>' +
            '</div>');
    }

    $.ajax({
        dataType: "json",
        url: "/virtual-pools",
        type: "GET",
        beforeSend: function () {
            toggleLoadingAnimation();
        }
    }).done(function (data) {
        clearCatalogs();
        $.each(data, function (index, value) {
            if (value['targetResponseTime'] === 0) {
                value['targetResponseTime'] = null
            }
            var appType = '';
            if (value['applicationsList'] && value['applicationsList']['appFreeFormlist']) {
                appType = 'Free form'
            }
            if (value['applicationsList'] && value['applicationsList']['appOracleOLTPlist']) {
                appType = 'Oracle OLTP'
            }
            addCatalog(value['id'], value['nodeId'], value['name'], value['targetResponseTime'], appType);
        });
        toggleLoadingAnimation();
    });
}

function fillNodeId() {
    $.ajax({
        url: '/nodeId',
        type: 'GET',
        contentType: 'application/json; charset=utf-8'
    }).done(function (data) {
        $('#header-id').text('Node: ' + data['nodeId'])
    });
}

$(document).ready(function () {
    fillNodeId();
    reloadServiceCatalogs();
    bindPoolClick();
});