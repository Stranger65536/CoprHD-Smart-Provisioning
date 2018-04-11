/**
 * Created by trofiv on 05.02.2015.
 */

function replaceAll(find, replace, str) {
    return str.replace(new RegExp(find, 'g'), replace);
}

function redrawVolumesAndHosts() {
    var volumesTable = $('#hostsAndVolumes');
    var waitImage = $('#wait-image');
    waitImage.show();
    volumesTable.hide();
    volumesTable.find("tbody").empty();
    $.ajax({
        url: "/block-volumes/volumesAndHosts",
        type: "GET"
    }).done(function (data) {
        $.each(data, function (i, obj) {
            volumesTable.find("tbody").append(
                '<tr>' +
                '<td class="lunPageTableCell" id="' + obj["host_id"] + '">' +
                obj["volume_name"] + '</td>' +
                '<td class="lunPageTableCell" id="' + obj["volume_id"] + '">' +
                (obj["host_name"] != null ? obj["host_name"] : "unmapped <a href=#><img src='/resources/images/attach.png'/></a>") +
                '</td>' +
                '</tr>');
            if (obj["host_id"] == null) {
                var hostId = 'td#' + replaceAll(":", '\\:', obj["volume_id"]);
                var image = $(hostId).find("a");
                image.on("click", function () {
                    showAttachLunToHostDiv(obj["volume_id"])
                });
            }
        });
        volumesTable.show();
        waitImage.hide();
    });
}

function fillHostList() {
    $.ajax({
        url: "/block-volumes/hosts",
        type: "GET"
    }).done(function (data) {
        $.each(data, function (i, host) {
            $("#attachingHost").append(
                '<option id="' + host["id"] + '">' + host["host_name"] + '</option>');
        });
    });
}

function hideAttachLunToHostDiv() {
    var attachLunToHostDiv = $("#attachLunToHostDiv");
    attachLunToHostDiv.removeAttr("volume_id");
    $("#content-area-wrapper").removeClass("blur");
    attachLunToHostDiv.hide();
}

function showAttachLunToHostDiv(volume_id) {
    var attachLunToHostDiv = $("#attachLunToHostDiv");
    attachLunToHostDiv.attr("volume_id", volume_id);
    $("#content-area-wrapper").addClass("blur");
    attachLunToHostDiv.show();
}

function bindButtons() {
    var button = $("#attach");
    button.button().click(function () {
        var volume_id = $("#attachLunToHostDiv").attr("volume_id");
        var host_id = $("select#attachingHost").find(":selected").attr("id");
        var request = {};
        request["volume_id"] = volume_id;
        request["host_id"] = host_id;
        hideAttachLunToHostDiv();
        $.ajax({
            url: "/block-volumes/export",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(request)
        }).done(function () {
            redrawVolumesAndHosts();
            swal({
                title: "LUN successfully exported!",
                type: "success",
                confirmButtonText: "OK"
            });
        }).fail(function () {
            swal({
                title: "Error during LUN exporting!",
                type: "error",
                confirmButtonText: "OK"
            });
        });
    });
    var cancel = $("#cancel");
    cancel.button().click(function () {
        hideAttachLunToHostDiv();
    });
}

$(document).ready(function () {
    redrawVolumesAndHosts();
    fillHostList();
    bindButtons();
});
