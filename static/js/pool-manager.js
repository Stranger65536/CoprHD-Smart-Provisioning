/**
 * Created by vladislav.trofimov@emc.com
 */

const panelPrefix = 'cos-characteristic-';
const panelButtonPrefix = 'cos-characteristic-button-';
const poolIndicatorPrefix = 'butthurt-indicator-';
const workloadPanelId = 'workload-panel-';
const workloadPanelButtonId = 'workload-panel-button-';

var pools;
var originalPools;
var appliedWorkload;

function isNumeric(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
}

function createPool() {
    var storagePoolIds = [];
    var workloadTypeSelect = $('#pool-manager-load-type');
    var workloadTypeSelectId = workloadTypeSelect.find(':selected').attr('id');
    var elements = $('#vp-list-table').find('tbody').find('input:checked');
    $.each(elements, function (index, value) {
        storagePoolIds.push($(value).attr('id'));
    });

    var responseTime = $('#performance-response-time').val();

    var request = {};
    request['storagePools'] = storagePoolIds;
    request['name'] = $('#pool-name').val();
    request['applicationsList'] = serializeWorkload()['applicationsList'];

    if (appliedWorkload === 'free-form') {
        request['applicationsList']['appOracleOLTPlist'] = [];
    } else if (appliedWorkload === 'app-oriented') {
        request['applicationsList']['appFreeFormlist'] = [];
    } else {
        request['applicationsList']['appOracleOLTPlist'] = [];
        request['applicationsList']['appFreeFormlist'] = [];
    }

    request['targetResponseTime'] = isNumeric(responseTime) ? responseTime : 0;

    disableInputs();

    swal({
        title: 'You are going to create Virtual Pool',
        text: 'Submit to perform request',
        type: 'info',
        showCancelButton: true,
        closeOnConfirm: false,
        showLoaderOnConfirm: true,
        confirmButtonText: 'Submit',
        preConfirm: function () {
            return new Promise(function (resolve) {
                swal.enableLoading();
                $.ajax({
                    url: '/virtual-pools',
                    data: JSON.stringify(request),
                    type: 'POST',
                    // async: false,
                    contentType: 'application/json; charset=utf-8'
                }).done(function () {
                    enableInputs();
                    swal({
                        title: 'Virtual Pool successfully created!',
                        type: 'success',
                        confirmButtonText: 'OK'
                    });
                }).fail(function () {
                    enableInputs();
                    swal({
                        title: 'Error processing create request!',
                        type: 'error',
                        confirmButtonText: 'OK'
                    });
                });
            });
        },
        allowOutsideClick: false
    }).then(function () {
        enableInputs();
    });
}

function processRootStorageProperty(name, dataStorageLosProperties, value) {
    if (name.indexOf('dataStorageLoS') > -1) {
        dataStorageLosProperties[name.replace('dataStorageLoS.', '')] = value;
        if (value == 'Unknown') {
            delete dataStorageLosProperties[name.replace('dataStorageLoS.', '')];
        }
        if (value == 'on') {
            dataStorageLosProperties['enabled'] = 'true';
        }
    }
}

function processRootPerformanceProperty(name, dataPerformanceLosProperties, value) {
    if (name.indexOf('dataPerformanceLoS') > -1) {
        dataPerformanceLosProperties[name.replace('dataPerformanceLoS.', '')] = value;
        if (value == 'Unknown') {
            delete dataPerformanceLosProperties[name.replace('dataPerformanceLoS.', '')];
        }
        if (name != 'dataPerformanceLoS.enabled' && value != 'Unknown') {
            dataPerformanceLosProperties['enabled'] = 'true';
        }
    }
}

function processRootAvailabilityProperty(name, dataAvailabilityLosProperties, value) {
    if (name.indexOf('dataAvailabilityLoS') > -1) {
        dataAvailabilityLosProperties[name.replace('dataAvailabilityLoS.', '')] = value;
        if (value == 'Unknown') {
            delete dataAvailabilityLosProperties[name.replace('dataAvailabilityLoS.', '')];
        }
        if (name != 'dataAvailabilityLoS.enabled' && value != 'Unknown') {
            dataAvailabilityLosProperties['enabled'] = 'true';
        }
    }
}

function processRootSnapshotsProperty(name, dataSnapshotLosProperties, value) {
    if (name.indexOf('dataSnapshotLoS') > -1) {
        dataSnapshotLosProperties[name.replace('dataSnapshotLoS.', '')] = value;
        if (value == 'Unknown') {
            delete dataSnapshotLosProperties[name.replace('dataSnapshotLoS.', '')];
        }
        if (name != 'dataSnapshotLoS.enabled' && value != 'Unknown') {
            dataSnapshotLosProperties["enabled"] = "true";
        }
    }
}

function processRootOtherProperty(name, json, value) {
    if (name.indexOf('.') === -1) {
        json = json.concat('"').concat(name).concat('"').concat(':').concat('"').concat(value).concat('"');
        json = json.concat(',');
    }
    return json;
}

function populateRootProperties(fields) {
    var dataStorageLosProperties = {};
    var dataPerformanceLosProperties = {};
    var dataAvailabilityLosProperties = {};
    var dataSnapshotLosProperties = {};

    var json = '{';
    $.each(fields, function (name, value) {
        json = processRootOtherProperty(name, json, value);
        processRootPerformanceProperty(name, dataPerformanceLosProperties, value);
        processRootAvailabilityProperty(name, dataAvailabilityLosProperties, value);
        processRootSnapshotsProperty(name, dataSnapshotLosProperties, value);
        processRootStorageProperty(name, dataStorageLosProperties, value);
    });

    return {
        dataStorageLosProperties: dataStorageLosProperties,
        dataPerformanceLosProperties: dataPerformanceLosProperties,
        dataAvailabilityLosProperties: dataAvailabilityLosProperties,
        dataSnapshotLosProperties: dataSnapshotLosProperties,
        json: json
    };
}

function processNestedPerformanceBlock(dataPerformanceLosProperties, json) {
    if (!$.isEmptyObject(dataPerformanceLosProperties)) {
        json = json.concat('"dataPerformanceLoS":{');
        $.each(dataPerformanceLosProperties, function (name, value) {
            json = json.concat('"').concat(name).concat('"').concat(':').concat('"').concat(value).concat('",');
        });
        json = json.substring(0, json.length - 1);
        json = json.concat('},');
    }
    return json;
}

function processNestedStorageBlock(dataStorageLosProperties, json) {
    if (!$.isEmptyObject(dataStorageLosProperties)) {
        json = json.concat('"dataStorageLoS":{');
        var supportedProtocols = [];
        $.each(dataStorageLosProperties, function (name, value) {
            if (name.indexOf('protocolCheck_') > -1) {
                supportedProtocols.push(name.replace('protocolCheck_', ''));
            } else {
                json = json.concat('"').concat(name).concat('"').concat(':').concat('"').concat(value).concat('",');
            }

        });
        if (supportedProtocols.length !== 0) {
            json = json.concat('"supportedAccessProtocols" : [');
            $.each(supportedProtocols, function (index, item) {
                json = json.concat('"').concat(item).concat('",');
            });
            json = json.substring(0, json.length - 1);
            json = json.concat("],")
        }
        json = json.substring(0, json.length - 1);
        json = json.concat('},');
    }
    return json;
}

function processNestedAvailabilityBlock(dataAvailabilityLosProperties, json) {
    if (!$.isEmptyObject(dataAvailabilityLosProperties)) {
        json = json.concat('"dataAvailabilityLoS":{');
        $.each(dataAvailabilityLosProperties, function (name, value) {
            json = json.concat('"').concat(name).concat('"').concat(':').concat('"').concat(value).concat('",');
        });
        json = json.substring(0, json.length - 1);
        json = json.concat('},');
    }
    return json;
}

function processNestedSnapshotsBlock(dataSnapshotLosProperties, json) {
    if (!$.isEmptyObject(dataSnapshotLosProperties)) {
        json = json.concat('"dataSnapshotLoS":{');
        $.each(dataSnapshotLosProperties, function (name, value) {
            json = json.concat('"').concat(name).concat('"').concat(':').concat('"').concat(value).concat('",');
        });
        json = json.substring(0, json.length - 1);
        json = json.concat('},');
    }
    return json;
}

function populateNestedProperties(dataPerformanceLosProperties, json, dataStorageLosProperties, dataAvailabilityLosProperties, dataSnapshotLosProperties) {
    json = processNestedPerformanceBlock(dataPerformanceLosProperties, json);
    json = processNestedAvailabilityBlock(dataAvailabilityLosProperties, json);
    json = processNestedSnapshotsBlock(dataSnapshotLosProperties, json);
    json = processNestedStorageBlock(dataStorageLosProperties, json);
    json = json.substring(0, json.length - 1);
    json = json.concat("}");
    return json;
}

function setPoolsVisible() {
    $('#pools-list-loading-overlay').hide();
    $('#ghost-row').show();
    $('#content-area-panel-content-table-body-content-left-cell-content-table-body-wrapper').removeClass('blur');
}

function setPoolsInvisible() {
    $('#content-area-panel-content-table-body-content-left-cell-content-table-body-wrapper').removeClass('blur').addClass('blur');
    $('#ghost-row').hide();
    $('#pools-list-loading-overlay').show();
}

function setInfoVisible() {
    $('#pool-info-loading-overlay').hide();
    $('#content-area-panel-content-table-body-content-right-cell-content').removeClass('blur');
}

function setInfoInvisible() {
    $('#content-area-panel-content-table-body-content-right-cell-content').removeClass('blur').addClass(('blur'));
    $('#pool-info-loading-overlay').show();
}

//noinspection FunctionWithMultipleReturnPointsJS
function serializeWorkload() {
    var workloadTypeSelect = $('#pool-manager-load-type');
    var workloadTypeSelectId = workloadTypeSelect.find(':selected').attr('id');
    var instancesCount = parseInt($('#instancesCount').val());
    var jsonResponse = {};
    var freeForm = [];
    var oracleOLTP = [];

    jsonResponse['applicationsList'] = {
        'appFreeFormlist': freeForm,
        'appOracleOLTPlist': oracleOLTP
    };

    //noinspection FunctionTooLongJS
    function parseOracleOLTP() {
        var oracleOLTPWorkload = {};
        oracleOLTPWorkload['type'] = 5;
        oracleOLTPWorkload['usersList'] = [];
        oracleOLTPWorkload['bThinLUN'] = false;
        oracleOLTPWorkload['oltpActiveTableGB'] = parseInt($('#oracle-oltp-active-table').val());
        oracleOLTPWorkload['oltpTransactionDuration'] = parseInt($('#oracle-oltp-transaction-duration').val());

        var webUsersCount = parseInt($('#oracle-oltp-web').val());
        if (webUsersCount > 0) {
            var webUsers = {};
            webUsers['userCount'] = webUsersCount;
            webUsers['userName'] = 'Web';
            webUsers['userType'] = 13;
            oracleOLTPWorkload['usersList'].push(webUsers);
        }

        var endUsersCount = parseInt($('#oracle-oltp-end-user').val());
        if (endUsersCount > 0) {
            var endUsers = {};
            endUsers['userCount'] = endUsersCount;
            endUsers['userName'] = 'End_user';
            endUsers['userType'] = 14;
            oracleOLTPWorkload['usersList'].push(endUsers);
        }

        var internalUsersCount = parseInt($('#oracle-oltp-internal').val());

        if (internalUsersCount > 0) {
            var internalUsers = {};
            internalUsers['userCount'] = internalUsersCount;
            internalUsers['userName'] = 'Internal';
            internalUsers['userType'] = 15;
            oracleOLTPWorkload['usersList'].push(internalUsers);
        }

        for (var i = 1; i <= instancesCount; i++) {
            var newOracleOLTPWorkload = $.extend(true, {}, oracleOLTPWorkload);
            newOracleOLTPWorkload['id'] = i;
            newOracleOLTPWorkload['appName'] = 'OLTP app ' + i;
            newOracleOLTPWorkload['description'] = 'OLTP app ' + i;
            oracleOLTP.push(newOracleOLTPWorkload);
        }
    }

    //noinspection FunctionTooLongJS
    function parseFreeForm() {
        var freeFormWorkload = {};
        freeFormWorkload['type'] = 9;
        freeFormWorkload['thinLUN'] = true;
        freeFormWorkload['bThinLUN'] = false;
        freeFormWorkload['deduplication'] = 0;
        freeFormWorkload['bFASTCache'] = false;
        freeFormWorkload['NumberLUNs'] = 1;
        freeFormWorkload['ConcurrentProcesses'] = 0;
        freeFormWorkload['ResponseTimeMS'] = 1000000;
        freeFormWorkload['PerformanceIOPS'] = parseInt($('#iops').val());

        var responseTimeSelect = $('#performance-response-time');
        var responseTime = responseTimeSelect.find(':selected').val();
        if (responseTime != 'Unknown') {
            freeFormWorkload['ResponseTimeMS'] = parseInt(responseTime);
        }

        freeFormWorkload['AllocatedGB'] = parseInt($('#capacity').val());
        freeFormWorkload['Skew'] = parseFloat($('#skew-activity').val()) / 100;
        freeFormWorkload['ReadSizeKB'] = 8;
        freeFormWorkload['WriteSizeKB'] = 8;
        freeFormWorkload['CacheHitReadPct'] = parseInt($('#read-hit-percent').val());
        freeFormWorkload['CacheHitWritePct'] = parseInt($('#write-hit-percent').val());
        freeFormWorkload['SequentialReadPct'] = parseInt($('#sequential-read-percent').val());
        freeFormWorkload['SequentialWritePct'] = parseInt($('#sequential-write-percent').val());
        freeFormWorkload['RandomReadPct'] = parseInt($('#random-read-percent').val());
        freeFormWorkload['RandomWritePct'] = parseInt($('#random-write-percent').val());

        for (var i = 1; i <= instancesCount; i++) {
            var newFreeForm = $.extend(true, {}, freeFormWorkload);
            newFreeForm['id'] = i;
            newFreeForm['appName'] = 'Free form app ' + i;
            newFreeForm['description'] = 'Free form app ' + i;
            freeForm.push(newFreeForm);
        }
    }

    switch (workloadTypeSelectId) {
        case 'free-form':
            parseFreeForm();
            return jsonResponse;
        case 'app-oriented':
            var appTypeSelect = $('#workload-app-oriented-type');
            var appTypeSelectId = appTypeSelect.find(':selected').attr('id');
            //noinspection NestedSwitchStatementJS
            switch (appTypeSelectId) {
                case 'oracle-oltp':
                    parseOracleOLTP();
                    return jsonResponse;
                default:
                    return {};
            }
        default:
            return {};
    }
}

//noinspection FunctionWithMultipleReturnPointsJS
function getUtilization(utilization) {
    //noinspection IfStatementWithTooManyBranchesJS
    if (utilization === null || utilization === undefined) {
        return 'Value missing in SRM';
    } else if (utilization < 1) {
        return utilization.toFixed(2) + '%';
    } else if (utilization <= 100) {
        return Math.round(utilization) + '%';
    } else {
        return "Doesn't fit";
    }
}

//noinspection FunctionWithMultipleReturnPointsJS
function getClass(rtime) {
    //noinspection IfStatementWithTooManyBranchesJS
    if (rtime <= 3) {
        return 'Diamond';
    } else if (rtime <= 6) {
        return 'Platinum';
    } else if (rtime <= 12) {
        return 'Gold';
    } else if (rtime <= 18) {
        return 'Silver';
    } else if (rtime <= 25) {
        return 'Bronze';
    } else if (rtime <= 100000) {
        return 'Teapot';
    } else {
        return '';
    }
}

function reloadPools() {
    //noinspection FunctionWithInconsistentReturnsJS,FunctionWithMultipleReturnPointsJS
    function isMatching(pool) {
        var responseTime = pool['responseTime'];
        var utilization = pool['utilization'];
        var targetResponseTime = $('#performance-response-time').val();
        var filterType = $('#filter-type').find(':selected').attr('id');

        //noinspection SwitchStatementWithNoDefaultBranchJS
        switch (filterType) {
            case 'filter-type-all':
                return true;
                break;
            case 'filter-type-matching':
                if (utilization >= 100) {
                    return false;
                }
                if (isNumeric(targetResponseTime)) {
                    return responseTime <= targetResponseTime;
                } else {
                    return true;
                }
                break;
        }
    }

    function redrawPoolsTable(poolsInfo) {
        var position = $('#vp-list-table').find('tbody');
        position.empty();

        //noinspection FunctionWithMultipleReturnPointsJS
        $.each(poolsInfo, function (index, pool) {
            if (!isMatching(pool)) {
                return;
            }

            var id = pool['id'];
            var name = pool['name'];
            var responseTime = pool['responseTime'];
            var utilization = pool['utilization'];
            var storage = pool['storageSystem'];

            //noinspection QuirksModeInspectionTool
            position.append("<tr id=" + id + ">" +
                "<td class='checkbox-cell'>" +
                "<label>" +
                "<input type='checkbox' id=" + id + ">" +
                "</label>" +
                "</td>" +
                "<td class='info-cell'>" +
                "<div class='info-cell-table'>" +
                "<div class='info-name-row'>" +
                "<div class='info-name-cell'>" +
                "<div class='info-name-table'>" +
                "<div class='name-row'>" +
                "<div class='pool-label-cell'>" +
                "Name:" +
                "</div>" +
                "<div class='pool-value-cell'>" +
                name +
                "</div>" +
                "</div>" +
                "<div class='name-row'>" +
                "<div class='pool-label-cell'>" +
                "Storage:" +
                "</div>" +
                "<div class='pool-value-cell'>" +
                storage +
                "</div>" +
                "</div>" +
                "<div class='name-row'>" +
                "<div class='pool-label-cell'>" +
                "Response time:" +
                "</div>" +
                "<div class='pool-value-cell'>" +
                (responseTime > 2000000000 ? "Doesn't fit" : Math.round(responseTime * 10) / 10 + "ms") +
                "</div>" +
                "</div>" +
                "<div class='name-row'>" +
                "<div class='pool-label-cell'>" +
                "Performance utilization:" +
                "</div>" +
                "<div class='pool-value-cell'>" +
                getUtilization(utilization) +
                "</div>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "<div class='info-characteristic-row'>" +
                "<div class='butthurt-indicator-cell'>" +
                "<div id='butthurt-indicator-" + id + "'" +
                "value='" + utilization + "'" +
                "class='butthurt-indicator'>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</td>" +
                "</tr>");
            setPoolIndicatorValue(id, utilization);
        });
    }

    function bindPoolSelect() {
        function ignoreCheckBoxClick() {
            var checkboxes = $('#vp-list-table').find('input[type="checkbox"]');
            checkboxes.on('click', function (e) {
                e.stopPropagation();
            });
        }

        var pools = $('#vp-list-table').find('tbody').find('tr');
        $.each(pools, function () {
            var id = $(this).attr('id');
            $(this).on('click', function () {
                $.ajax({
                    url: '/storage-pools/' + id,
                    type: 'GET',
                    dataType: 'json',
                    beforeSend: function () {
                        setInfoInvisible();
                    }
                }).done(function (data) {
                    setTimeout(function () {
                        displayTree(data);
                        setInfoVisible();
                    }, 1000);
                }).fail(function () {
                    setInfoVisible();
                    swal({
                        title: 'Error processing request!',
                        type: 'error',
                        confirmButtonText: 'OK'
                    });
                });
            })
        });
        ignoreCheckBoxClick();
    }

    redrawPoolsTable(pools);
    bindPoolSelect();
    customCheckbox();
    setPoolsVisible();
}

function processPoolSizingRequest() {
    function preparePools(data, originalPools) {
        $.each(data, function (index, pool) {
            var originalPool = originalPools[index];
            pool['utilization'] += originalPool['utilization'];
            pool['responseTime'] += originalPool['responseTime'];
        });
    }

    $.ajax({
        contentType: 'application/json',
        url: '/pool-manager/workload',
        type: 'POST',
        data: JSON.stringify(serializeWorkload()),
        beforeSend: function () {
            disableInputs();
            setPoolsInvisible();
        }
    }).done(function (data) {
        enableInputs();
        preparePools(data, originalPools);
        pools = data;
        reloadPools();
    }).fail(function () {
        enableInputs();
        setPoolsVisible();
        swal({
            title: 'Error processing sizing request!',
            type: 'error',
            confirmButtonText: 'OK'
        });
    });
}

function processGetPoolsRequest() {
    disableInputs();

    $.ajax({
        dataType: 'json',
        url: '/storage-pools/',
        type: 'GET'
    }).done(function (data) {
        enableInputs();
        originalPools = data;
        pools = data;
        reloadPools();
    }).fail(function () {
        enableInputs();
        setPoolsVisible();
        swal({
            title: 'Error processing request!',
            text: 'Please, reload the page ad check that server is running!',
            type: 'error',
            confirmButtonText: 'OK'
        });
    });
}

function bindInputs() {
    $('#los-dropdowns').find('select, input').on('change', function () {
        updateLosOptionPanel($(this));
    });
    var rt = $('#performance-response-time');
    rt.on('change', function () {
        reloadPools();
    });
}

function constructNode(data, obj) {
    var node;
    $.each(obj, function (key, value) {
        node = {};
        if (typeof  value == 'object' && value !== null) {
            node.label = key;
            var children = [];
            node.children = children;
            constructNode(children, value);
        } else {
            node.label = key + ': ' + value;
        }
        data.push(node);
    });
}

function formatData(arr) {
    var data = [];
    if (!$.isArray(arr)) {
        arr = [arr];
    }
    for (var i = 0; i < arr.length; i++) {
        var obj = arr[i];
        constructNode(data, obj);
    }
    return data;
}

function poolNameIsEmpty() {
    var field = $('#pool-name');
    return $(field).hasClass('empty');
}

function bindEmptyPoolNamePlaceholder() {
    var field = $('#pool-name');
    $(field).focus(function () {
        if (poolNameIsEmpty()) {
            $(field).val('');
            $(field).removeClass('empty');
        }
    });
    $(field).blur(function () {
        if ($(field).val().length === 0) {
            $(field).removeClass('empty').addClass('empty');
            $(field).val('Pool name');
        }
    });
}

function bindCreatePoolButton() {
    var button = $('#create-pool-button');
    button.css('margin-bottom', '15px');
    button.button().click(function () {
        if (poolNameIsEmpty()) {
            swal({
                title: 'Enter Pool name!',
                type: 'error',
                confirmButtonText: 'OK'
            });
        } else {
            createPool();
        }
    });
}

function applyWorkload() {
    var workloadTypeSelect = $('#pool-manager-load-type');
    var value = workloadTypeSelect.find(':selected').text();
    var addResult = addWorkloadOptionPanel(value);
    var workloadPanelId = addResult[0];
    var workloadButtonId = addResult[1];
    appliedWorkload = workloadTypeSelect.find('option:selected').attr('id');
    bindClearWorkloadPanelButton(workloadPanelId, workloadButtonId);
    processPoolSizingRequest();
}

function bindWorkloadButtons() {
    $('#apply-workload-button').button().click(function () {
        applyWorkload();
    });
    $('#reset-workload-button').button().click(function () {
        removeWorkloadPanel();
        pools = originalPools;
        appliedWorkload = null;
        reloadPools();
    });
}

//noinspection FunctionWithMultipleReturnPointsJS
function getLosOptionInfo(losOption) {
    var isOptionSet, losOptionValue;
    var losOptionId = $(losOption).attr('id');
    var losOptionRow = $(losOption).closest('.dropdown-row');
    var losOptionName = $(losOptionRow).find('.dropdown-title-cell').text().trim();

    if ($(losOptionRow).hasClass('list-option')) {
        isOptionSet = $(losOption).is(':checked');
        var losOptionItem = $(losOption).closest('.list-option-item');
        losOptionValue = $(losOptionItem).find('.list-option-item-value').text().trim();
        return [losOptionId, isOptionSet, losOptionName, losOptionValue];
    } else {
        isOptionSet = $(losOption)[0].selectedIndex !== 0;
        losOptionValue = $(losOption).find(':selected').text();
        return [losOptionId, isOptionSet, losOptionName, losOptionValue];
    }
}

$.fn.exists = function () {
    return this.length !== 0;
};

function addWorkloadOptionPanel(value) {
    var characteristicsPanel = $('#content-area-panel-content-table-footer-table-characteristics-cell');
    var workloadPanel = $('#' + workloadPanelId);
    if ($(workloadPanel).exists()) {
        workloadPanel.empty();
        workloadPanel.append("Typical workload" + " : " + value + "\n" +
            '<input type="image" ' +
            'id="' + workloadPanelButtonId + '" ' +
            'class="disable-image" ' +
            'src="resources/images/error.png"/>\n');
        workloadPanel.effect('highlight', 1500);
    } else {
        characteristicsPanel.append('<div class="characteristic-wrapper" style="display: none;" id=' + workloadPanelId + '>\n' +
            "Typical workload" + " : " + value + "\n" +
            '<input type="image" ' +
            'id="' + workloadPanelButtonId + '" ' +
            'class="disable-image" ' +
            'src="resources/images/error.png"/>\n' +
            '</div>\n');
        characteristicsPanel.find('#' + workloadPanelId).fadeIn(250);
    }
    return [workloadPanelId, workloadPanelButtonId];
}

function addLosOptionPanel(losOptionId, losOptionName, losOptionValue) {
    var characteristicsPanel = $('#content-area-panel-content-table-footer-table-characteristics-cell');
    var losOptionPanelId = panelPrefix + losOptionId;
    var losOptionButtonId = panelButtonPrefix + losOptionId;
    var losOptionPanel = characteristicsPanel.find('#' + losOptionPanelId);

    if ($(losOptionPanel).exists()) {
        losOptionPanel.empty();
        losOptionPanel.append(losOptionName + " : " + losOptionValue + "\n" +
            '<input type="image" ' +
            'id="' + losOptionButtonId + '" ' +
            'class="disable-image" ' +
            'src="resources/images/error.png"/>\n');
        losOptionPanel.effect('highlight', 1500);
    } else {
        characteristicsPanel.append('<div class="characteristic-wrapper" style="display: none;" id=' + losOptionPanelId + '>\n' +
            losOptionName + " : " + losOptionValue + "\n" +
            '<input type="image" ' +
            'id="' + losOptionButtonId + '" ' +
            'class="disable-image" ' +
            'src="resources/images/error.png"/>\n' +
            '</div>\n');
        characteristicsPanel.find('#' + losOptionPanelId).fadeIn(250);
    }

    return [losOptionPanelId, losOptionButtonId];
}

function removeLosOptionPanel(losOptionId) {
    var losOptionPanelId = panelPrefix + losOptionId;
    var losOptionPanel = $('#' + losOptionPanelId);
    if ($(losOptionPanel).exists()) {
        $(losOptionPanel).fadeOut(250, function () {
            $(this).remove();
        });
    }
}

function removeWorkloadPanel() {
    var workloadPanel = $('#' + workloadPanelId);
    if (workloadPanel.exists()) {
        workloadPanel.fadeOut(250, function () {
            $(this).remove();
        });
    }
}

function updateLosOptionPanel(losOption) {
    var losOptionInfo = getLosOptionInfo(losOption);
    var losOptionId = losOptionInfo[0];
    var isOptionSet = losOptionInfo[1];
    var losOptionName = losOptionInfo[2];
    var losOptionValue = losOptionInfo[3];

    if (isOptionSet) {
        var addResult = addLosOptionPanel(losOptionId, losOptionName, losOptionValue);
        var losOptionPanelId = addResult[0];
        var losOptionButtonId = addResult[1];
        bindClearLosOptionButton(losOptionId, losOptionPanelId, losOptionButtonId)
    } else {
        removeLosOptionPanel(losOptionId);
    }
}

function bindClearWorkloadPanelButton(panelId, buttonId) {
    $('#' + buttonId).on('click', function () {
        $('#' + panelId).fadeOut(250, function () {
            $(this).remove();
            pools = originalPools;
            reloadPools();
        });
    });
}

function bindClearLosOptionButton(losOptionId, panelId, buttonId) {
    $('#' + buttonId).on('click', function () {
        var losOption = $('#' + losOptionId);
        losOption.prop('checked', false);
        losOption.parent().removeClass('selected');
        losOption[0].selectedIndex = 0;
        $('#' + panelId).fadeOut(250, function () {
            $(this).remove();
            reloadPools();
        });
    });
}

function bindFilterType() {
    var dropdown = $('#filter-type');
    dropdown.on('change', function () {
        reloadPools();
    });
}

function displayTree(data) {
    var infoTree = $('#pool-info-tree');
    infoTree.tree({
        selectable: false
    });
    infoTree.tree('loadData', formatData(data));
}

function bindTreeClick() {
    var infoTree = $('#pool-info-tree');
    infoTree.tree({
        selectable: false
    });
    infoTree.bind('tree.click', function (e) {
        infoTree.tree('toggle', e.node);
    });
}

function eventIsExecuteOnBlock(eventBlock, event) {
    return $(eventBlock).is(event.target) || !($(eventBlock).has(event.target).length === 0);
}

var autoHidePanels = {};

//noinspection FunctionNamingConventionJS
function startWatchingOnAutoHideMenuPanels() {
    function resetAllTogglersState(panelProperties) {
        $.each(panelProperties['togglers'], function (togglerBlock, togglerProperties) {
            var cssBlock = togglerProperties['actionBlock'];
            var cssProperty = togglerProperties['css']['property'];
            panelProperties['opened'] = false;
            $(cssBlock).css(cssProperty, '');
        });
    }

    function processClickOnUnknownBlock() {
        $.each(autoHidePanels, function (panel, panelProperties) {
            resetAllTogglersState(panelProperties);
            $(panel).slideUp();
        });
    }

    function setCurrentTogglerState(panelProperties, cssBlock, cssProperty, cssValue) {
        panelProperties['opened'] = true;
        $(cssBlock).css(cssProperty, cssValue);
    }

    function resetCurrentTogglerState(panelProperties, cssBlock, cssProperty) {
        panelProperties['opened'] = false;
        $(cssBlock).css(cssProperty, '');
    }

    function processPanelTogglers(panelProperties, e, handlerFound, panel) {
        //noinspection FunctionWithInconsistentReturnsJS,FunctionWithMultipleReturnPointsJS
        $.each(panelProperties['togglers'], function (togglerBlock, togglerProperties) {
            if (eventIsExecuteOnBlock(togglerBlock, e)) {
                handlerFound = true;
                var cssBlock = togglerProperties['actionBlock'];
                var cssProperty = togglerProperties['css']['property'];
                var cssValue = togglerProperties['css']['value'];
                if (panelProperties['opened'] === false) {
                    setCurrentTogglerState(panelProperties, cssBlock, cssProperty, cssValue);
                    $(panel).slideDown();
                } else {
                    resetCurrentTogglerState(panelProperties, cssBlock, cssProperty);
                    $(panel).slideUp();
                }
                return false;
            }
        });
        return handlerFound;
    }

    function processPanelExceptions(panelProperties, e, handlerFound) {
        //noinspection FunctionWithInconsistentReturnsJS,FunctionWithMultipleReturnPointsJS
        $.each(panelProperties['exceptions'], function (index, panelException) {
            if (eventIsExecuteOnBlock(panelException, e)) {
                handlerFound = true;
                return false;
            }
        });
        return handlerFound;
    }

    $(document).mousedown(function (e) {
        var handlerFound = false;
        //noinspection FunctionWithInconsistentReturnsJS,FunctionWithMultipleReturnPointsJS
        $.each(autoHidePanels, function (panel, panelProperties) {
            handlerFound = processPanelTogglers(panelProperties, e, handlerFound, panel);
            if (handlerFound) {
                return false;
            } else {
                handlerFound = processPanelExceptions(panelProperties, e, handlerFound);
                if (handlerFound) {
                    return false;
                }
            }
        });
        if (!handlerFound) {
            processClickOnUnknownBlock();
        }
    });
}

function addAutoHideMenuPanels(menuPanelIdList) {
    $.each(menuPanelIdList, function (index, menuPanelId) {
        if (!(menuPanelId in autoHidePanels)) {
            autoHidePanels[menuPanelId] = {
                exceptions: [],
                togglers: {},
                opened: false
            };
        }
    });
}

function addExceptionsToAutoHideMenuPanel(menuPanelId, exceptionBlockIdList) {
    if ((menuPanelId in autoHidePanels)) {
        $.each(exceptionBlockIdList, function (index, exceptionBlockId) {
            autoHidePanels[menuPanelId]['exceptions'].push(exceptionBlockId);
        });
    }
}

function addTogglersToAutoHideMenuPanel(menuPanelId, togglerList) {
    $.each(togglerList, function (index, togglerInfo) {
        var togglerBlockId = togglerInfo[0],
            toggleActionBlock = togglerInfo[1],
            toggleCssProperty = togglerInfo[2],
            toggleCssValue = togglerInfo[3];
        if ((menuPanelId in autoHidePanels)) {
            autoHidePanels[menuPanelId]['togglers'][togglerBlockId] = {
                actionBlock: toggleActionBlock,
                css: {
                    property: toggleCssProperty,
                    value: toggleCssValue
                }
            };
        }
    });
}

function bindAutoHideMenuPanels() {
    addAutoHideMenuPanels([
        '#content-area-panel-content-table-header-menu-performance-dropdown-wrapper',
        '#content-area-panel-content-table-header-menu-availability-dropdown-wrapper',
        '#content-area-panel-content-table-header-menu-snapshots-dropdown-wrapper',
        '#content-area-panel-content-table-header-menu-storage-dropdown-wrapper'
    ]);
}

//noinspection FunctionNamingConventionJS
function bindExceptionsForAutoHideMenuPanels() {
    var exceptionBlocks = [
        '#content-area-panel-content-table-header-menu-performance-dropdown-wrapper',
        '#content-area-panel-content-table-header-menu-availability-dropdown-wrapper',
        '#content-area-panel-content-table-header-menu-snapshots-dropdown-wrapper',
        '#content-area-panel-content-table-header-menu-storage-dropdown-wrapper'
    ];
    addExceptionsToAutoHideMenuPanel('#content-area-panel-content-table-header-menu-performance-dropdown-wrapper', exceptionBlocks);
    addExceptionsToAutoHideMenuPanel('#content-area-panel-content-table-header-menu-availability-dropdown-wrapper', exceptionBlocks);
    addExceptionsToAutoHideMenuPanel('#content-area-panel-content-table-header-menu-snapshots-dropdown-wrapper', exceptionBlocks);
    addExceptionsToAutoHideMenuPanel('#content-area-panel-content-table-header-menu-storage-dropdown-wrapper', exceptionBlocks);
}

//noinspection FunctionNamingConventionJS
function bindTogglersForAutoHideMenuPanels() {
    addTogglersToAutoHideMenuPanel('#content-area-panel-content-table-header-menu-performance-dropdown-wrapper', [[
        '#content-area-panel-content-table-header-menu-performance-item',
        '#content-area-panel-content-table-header-menu-performance',
        'background-color',
        'rgba(43, 114, 185, 1)'
    ]]);
    addTogglersToAutoHideMenuPanel('#content-area-panel-content-table-header-menu-availability-dropdown-wrapper', [[
        '#content-area-panel-content-table-header-menu-availability-item',
        '#content-area-panel-content-table-header-menu-availability',
        'background-color',
        'rgba(43, 114, 185, 1)'
    ]]);
    addTogglersToAutoHideMenuPanel('#content-area-panel-content-table-header-menu-snapshots-dropdown-wrapper', [[
        '#content-area-panel-content-table-header-menu-snapshots-item',
        '#content-area-panel-content-table-header-menu-snapshots',
        'background-color',
        'rgba(43, 114, 185, 1)'
    ]]);
    addTogglersToAutoHideMenuPanel('#content-area-panel-content-table-header-menu-storage-dropdown-wrapper', [[
        '#content-area-panel-content-table-header-menu-storage-item',
        '#content-area-panel-content-table-header-menu-storage',
        'background-color',
        'rgba(43, 114, 185, 1)'
    ]]);
}

function bindCollapseMenuButtons() {

    function bindOpenCollapsiblePanelButton() {
        $('#content-area-panel-content-table-body-content-collapse-cell-button').on('click', function () {
            var panel = $('#content-area-panel-content-table-body-content-collapse-cell');
            //noinspection JSValidateTypes
            panel.children().fadeOut(250).promise().done(function () {
                $('#content-area-panel-content-table-body-content-collapse-cell-content-wrapper').removeClass('hidden');
                panel.animate({
                    'width': '400px',
                    'min-width': '400px',
                    'max-width': '400px'
                }, 250);
                $('#pool-info-loading-overlay').removeClass('thin').addClass('thin');
            });
        });
    }

    function bindCloseCollapsiblePanelButton() {
        $('#content-area-panel-content-table-body-content-close-cell-button').on('click', function () {
            var panel = $('#content-area-panel-content-table-body-content-collapse-cell');
            panel.animate({
                'width': '50px',
                'min-width': '50px',
                'max-width': '50px'
            }, 250).promise().done(function () {
                $('#content-area-panel-content-table-body-content-collapse-cell-content-wrapper').addClass('hidden');
                $('#content-area-panel-content-table-body-content-collapse-cell-label').show();
                $('#content-area-panel-content-table-body-content-collapse-cell-button').show();
                $('#pool-info-loading-overlay').removeClass('thin');
            });
        });
    }

    bindOpenCollapsiblePanelButton();
    bindCloseCollapsiblePanelButton();
}

function replaceAll(find, replace, str) {
    return str.replace(new RegExp(find, 'g'), replace);
}

//noinspection FunctionWithMultipleReturnPointsJS
function getColorByValue(value) {
    if (value < 50) {
        return 'rgba(0, 255, 0, 0.55)';
    } else if (value >= 90) {
        return 'rgba(255, 0, 0, 0.55)';
    } else {
        return 'rgba(255, 153, 51, 0.55)'
    }
}

function setPoolIndicatorValue(poolId, value) {
    var indicatorId = '#' + poolIndicatorPrefix + replaceAll(':', '\\:', poolId);
    var indicator = $(indicatorId);
    indicator.progressbar({
        value: value
    });
    indicator.find('.ui-progressbar-value').css('background', getColorByValue(value));
}

function bindAppTypeSelect() {
    var appTypeSelect = $('#workload-app-oriented-type');

    appTypeSelect.on('change', function () {
        var optionId = appTypeSelect.find('option:selected').attr('id');
        var oracleOLTP = $('.workload-app-oriented-table.oracle-oltp');
        var oracleOLTPLabel = $('.workload-app-oriented-label.oracle-oltp');
        var oracleDWSE = $('.workload-app-oriented-table.oracle-dwse');
        var oracleDWSELabel = $('.workload-app-oriented-label.oracle-dwse');
        var virtualDesktopSimple = $('.workload-app-oriented-table.virtual-desktop-simple');
        var virtualDesktopSimpleLabel = $('.workload-app-oriented-label.virtual-desktop-simple');
        //noinspection SwitchStatementWithNoDefaultBranchJS
        switch (optionId) {
            case 'oracle-oltp':
                oracleDWSE.hide();
                oracleDWSELabel.hide();
                virtualDesktopSimple.hide();
                virtualDesktopSimpleLabel.hide();
                oracleOLTP.show();
                oracleOLTPLabel.show();
                break;
            case 'oracle-dwse':
                oracleOLTP.hide();
                oracleOLTPLabel.hide();
                virtualDesktopSimple.hide();
                virtualDesktopSimpleLabel.hide();
                oracleDWSE.show();
                oracleDWSELabel.show();
                break;
            case 'virtual-desktop-simple':
                oracleDWSE.hide();
                oracleDWSELabel.hide();
                oracleOLTP.hide();
                oracleOLTPLabel.hide();
                virtualDesktopSimple.show();
                virtualDesktopSimpleLabel.show();
                break;
        }
    });
}

function bindWorkloadTypeSelect() {
    var loadTypeSelect = $('#pool-manager-load-type');

    loadTypeSelect.on('change', function () {
        var optionId = loadTypeSelect.find('option:selected').attr('id');
        var appType = $('.workload-app-oriented-type');
        var freeForm = $('.workload-free-form-table');
        var oracleOLTP = $('.workload-app-oriented-table.oracle-oltp');
        var oracleOLTPLabel = $('.workload-app-oriented-label.oracle-oltp');
        var oracleDWSE = $('.workload-app-oriented-table.oracle-dwse');
        var oracleDWSELabel = $('.workload-app-oriented-label.oracle-dwse');
        var virtualDesktopSimple = $('.workload-app-oriented-table.virtual-desktop-simple');
        var virtualDesktopSimpleLabel = $('.workload-app-oriented-label.virtual-desktop-simple');

        //noinspection SwitchStatementWithNoDefaultBranchJS
        switch (optionId) {
            case 'free-form':
                appType.hide();
                oracleOLTP.hide();
                oracleOLTPLabel.hide();
                oracleDWSE.hide();
                oracleDWSELabel.hide();
                virtualDesktopSimple.hide();
                virtualDesktopSimpleLabel.hide();
                freeForm.show();
                break;
            case 'app-oriented':
                freeForm.hide();
                appType.show();
                oracleOLTP.show();
                oracleOLTPLabel.show();
                break;
        }
    });
}

function disableInputs() {
    $('input[type=button]').button().attr("disabled", true).addClass("ui-state-disabled").addClass("ui-button-disabled");
    $('input[type=checkbox]').prop('disabled', 'disabled');
    $('input[type=number]').prop('disabled', 'disabled');
    $('input[type=text]').prop('disabled', 'disabled');
    $('select').prop('disabled', 'disabled');
}

function enableInputs() {
    $('input[type=button]').button().attr("disabled", false).removeClass("ui-state-disabled").removeClass("ui-button-disabled");
    $('input[type=checkbox]').prop('disabled', '');
    $('input[type=number]').prop('disabled', '');
    $('input[type=text]').prop('disabled', '');
    $('select').prop('disabled', '');
    $("input#skew-capacity").prop('disabled', 'disabled');
}

function enableSkewWatcher() {
    var skewActivity = $('#skew-activity');
    var skewCapacity = $('#skew-capacity');

    skewActivity.on('change', function () {
        skewCapacity.val(100 - skewActivity.val());
    });
}

$(document).ready(function () {
    processGetPoolsRequest();

    enableSkewWatcher();
    bindEmptyPoolNamePlaceholder();
    bindTreeClick();
    bindCreatePoolButton();
    bindCollapseMenuButtons();
    bindWorkloadTypeSelect();
    bindAppTypeSelect();

    bindFilterType();
    bindWorkloadButtons();
    bindInputs();

    startWatchingOnAutoHideMenuPanels();
    bindAutoHideMenuPanels();
    bindExceptionsForAutoHideMenuPanels();
    bindTogglersForAutoHideMenuPanels();
});