(function e(t, n, r) {
    function s(o, u) {
        if (!n[o]) {
            if (!t[o]) {
                var a = typeof require == "function" && require;
                if (!u && a) return a(o, !0);
                if (i) return i(o, !0);
                throw new Error("Cannot find module '" + o + "'")
            }
            var f = n[o] = {exports: {}};
            t[o][0].call(f.exports, function (e) {
                var n = t[o][1][e];
                return s(n ? n : e)
            }, f, f.exports, e, t, n, r)
        }
        return n[o].exports
    }

    var i = typeof require == "function" && require;
    for (var o = 0; o < r.length; o++) s(r[o]);
    return s
})({
    1: [function (require, module, exports) {
        (function () {
            var DragAndDropHandler, DragElement, HitAreasGenerator, Position, VisibleNodeIterator, node_module,
                __hasProp = {}.hasOwnProperty,
                __extends = function (child, parent) {
                    for (var key in parent) {
                        if (__hasProp.call(parent, key)) child[key] = parent[key];
                    }

                    function ctor() {
                        this.constructor = child;
                    }

                    ctor.prototype = parent.prototype;
                    child.prototype = new ctor();
                    child.__super__ = parent.prototype;
                    return child;
                };

            node_module = require('./node');

            Position = node_module.Position;

            DragAndDropHandler = (function () {
                function DragAndDropHandler(tree_widget) {
                    this.tree_widget = tree_widget;
                    this.hovered_area = null;
                    this.$ghost = null;
                    this.hit_areas = [];
                    this.is_dragging = false;
                    this.current_item = null;
                }

                DragAndDropHandler.prototype.mouseCapture = function (position_info) {
                    var $element, node_element;
                    $element = $(position_info.target);
                    if (!this.mustCaptureElement($element)) {
                        return null;
                    }
                    if (this.tree_widget.options.onIsMoveHandle && !this.tree_widget.options.onIsMoveHandle($element)) {
                        return null;
                    }
                    node_element = this.tree_widget._getNodeElement($element);
                    if (node_element && this.tree_widget.options.onCanMove) {
                        if (!this.tree_widget.options.onCanMove(node_element.node)) {
                            node_element = null;
                        }
                    }
                    this.current_item = node_element;
                    return this.current_item !== null;
                };

                DragAndDropHandler.prototype.mouseStart = function (position_info) {
                    var offset;
                    this.refresh();
                    offset = $(position_info.target).offset();
                    this.drag_element = new DragElement(this.current_item.node, position_info.page_x - offset.left, position_info.page_y - offset.top, this.tree_widget.element);
                    this.is_dragging = true;
                    this.current_item.$element.addClass('jqtree-moving');
                    return true;
                };

                DragAndDropHandler.prototype.mouseDrag = function (position_info) {
                    var area, can_move_to;
                    this.drag_element.move(position_info.page_x, position_info.page_y);
                    area = this.findHoveredArea(position_info.page_x, position_info.page_y);
                    can_move_to = this.canMoveToArea(area);
                    if (can_move_to && area) {
                        if (!area.node.isFolder()) {
                            this.stopOpenFolderTimer();
                        }
                        if (this.hovered_area !== area) {
                            this.hovered_area = area;
                            if (this.mustOpenFolderTimer(area)) {
                                this.startOpenFolderTimer(area.node);
                            } else {
                                this.stopOpenFolderTimer();
                            }
                            this.updateDropHint();
                        }
                    } else {
                        this.removeHover();
                        this.removeDropHint();
                        this.stopOpenFolderTimer();
                    }
                    return true;
                };

                DragAndDropHandler.prototype.mustCaptureElement = function ($element) {
                    return !$element.is('input,select');
                };

                DragAndDropHandler.prototype.canMoveToArea = function (area) {
                    var position_name;
                    if (!area) {
                        return false;
                    } else if (this.tree_widget.options.onCanMoveTo) {
                        position_name = Position.getName(area.position);
                        return this.tree_widget.options.onCanMoveTo(this.current_item.node, area.node, position_name);
                    } else {
                        return true;
                    }
                };

                DragAndDropHandler.prototype.mouseStop = function (position_info) {
                    this.moveItem(position_info);
                    this.clear();
                    this.removeHover();
                    this.removeDropHint();
                    this.removeHitAreas();
                    if (this.current_item) {
                        this.current_item.$element.removeClass('jqtree-moving');
                        this.current_item = null;
                    }
                    this.is_dragging = false;
                    return false;
                };

                DragAndDropHandler.prototype.refresh = function () {
                    this.removeHitAreas();
                    if (this.current_item) {
                        this.generateHitAreas();
                        this.current_item = this.tree_widget._getNodeElementForNode(this.current_item.node);
                        if (this.is_dragging) {
                            return this.current_item.$element.addClass('jqtree-moving');
                        }
                    }
                };

                DragAndDropHandler.prototype.removeHitAreas = function () {
                    return this.hit_areas = [];
                };

                DragAndDropHandler.prototype.clear = function () {
                    this.drag_element.remove();
                    return this.drag_element = null;
                };

                DragAndDropHandler.prototype.removeDropHint = function () {
                    if (this.previous_ghost) {
                        return this.previous_ghost.remove();
                    }
                };

                DragAndDropHandler.prototype.removeHover = function () {
                    return this.hovered_area = null;
                };

                DragAndDropHandler.prototype.generateHitAreas = function () {
                    var hit_areas_generator;
                    hit_areas_generator = new HitAreasGenerator(this.tree_widget.tree, this.current_item.node, this.getTreeDimensions().bottom);
                    return this.hit_areas = hit_areas_generator.generate();
                };

                DragAndDropHandler.prototype.findHoveredArea = function (x, y) {
                    var area, dimensions, high, low, mid;
                    dimensions = this.getTreeDimensions();
                    if (x < dimensions.left || y < dimensions.top || x > dimensions.right || y > dimensions.bottom) {
                        return null;
                    }
                    low = 0;
                    high = this.hit_areas.length;
                    while (low < high) {
                        mid = (low + high) >> 1;
                        area = this.hit_areas[mid];
                        if (y < area.top) {
                            high = mid;
                        } else if (y > area.bottom) {
                            low = mid + 1;
                        } else {
                            return area;
                        }
                    }
                    return null;
                };

                DragAndDropHandler.prototype.mustOpenFolderTimer = function (area) {
                    var node;
                    node = area.node;
                    return node.isFolder() && !node.is_open && area.position === Position.INSIDE;
                };

                DragAndDropHandler.prototype.updateDropHint = function () {
                    var node_element;
                    if (!this.hovered_area) {
                        return;
                    }
                    this.removeDropHint();
                    node_element = this.tree_widget._getNodeElementForNode(this.hovered_area.node);
                    return this.previous_ghost = node_element.addDropHint(this.hovered_area.position);
                };

                DragAndDropHandler.prototype.startOpenFolderTimer = function (folder) {
                    var openFolder;
                    openFolder = (function (_this) {
                        return function () {
                            return _this.tree_widget._openNode(folder, _this.tree_widget.options.slide, function () {
                                _this.refresh();
                                return _this.updateDropHint();
                            });
                        };
                    })(this);
                    this.stopOpenFolderTimer();
                    return this.open_folder_timer = setTimeout(openFolder, this.tree_widget.options.openFolderDelay);
                };

                DragAndDropHandler.prototype.stopOpenFolderTimer = function () {
                    if (this.open_folder_timer) {
                        clearTimeout(this.open_folder_timer);
                        return this.open_folder_timer = null;
                    }
                };

                DragAndDropHandler.prototype.moveItem = function (position_info) {
                    var doMove, event, moved_node, position, previous_parent, target_node;
                    if (this.hovered_area && this.hovered_area.position !== Position.NONE && this.canMoveToArea(this.hovered_area)) {
                        moved_node = this.current_item.node;
                        target_node = this.hovered_area.node;
                        position = this.hovered_area.position;
                        previous_parent = moved_node.parent;
                        if (position === Position.INSIDE) {
                            this.hovered_area.node.is_open = true;
                        }
                        doMove = (function (_this) {
                            return function () {
                                _this.tree_widget.tree.moveNode(moved_node, target_node, position);
                                _this.tree_widget.element.empty();
                                return _this.tree_widget._refreshElements();
                            };
                        })(this);
                        event = this.tree_widget._triggerEvent('tree.move', {
                            move_info: {
                                moved_node: moved_node,
                                target_node: target_node,
                                position: Position.getName(position),
                                previous_parent: previous_parent,
                                do_move: doMove,
                                original_event: position_info.original_event
                            }
                        });
                        if (!event.isDefaultPrevented()) {
                            return doMove();
                        }
                    }
                };

                DragAndDropHandler.prototype.getTreeDimensions = function () {
                    var offset;
                    offset = this.tree_widget.element.offset();
                    return {
                        left: offset.left,
                        top: offset.top,
                        right: offset.left + this.tree_widget.element.width(),
                        bottom: offset.top + this.tree_widget.element.height() + 16
                    };
                };

                return DragAndDropHandler;

            })();

            VisibleNodeIterator = (function () {
                function VisibleNodeIterator(tree) {
                    this.tree = tree;
                }

                VisibleNodeIterator.prototype.iterate = function () {
                    var is_first_node, _iterateNode;
                    is_first_node = true;
                    _iterateNode = (function (_this) {
                        return function (node, next_node) {
                            var $element, child, children_length, i, must_iterate_inside, _i, _len, _ref;
                            must_iterate_inside = (node.is_open || !node.element) && node.hasChildren();
                            if (node.element) {
                                $element = $(node.element);
                                if (!$element.is(':visible')) {
                                    return;
                                }
                                if (is_first_node) {
                                    _this.handleFirstNode(node, $element);
                                    is_first_node = false;
                                }
                                if (!node.hasChildren()) {
                                    _this.handleNode(node, next_node, $element);
                                } else if (node.is_open) {
                                    if (!_this.handleOpenFolder(node, $element)) {
                                        must_iterate_inside = false;
                                    }
                                } else {
                                    _this.handleClosedFolder(node, next_node, $element);
                                }
                            }
                            if (must_iterate_inside) {
                                children_length = node.children.length;
                                _ref = node.children;
                                for (i = _i = 0, _len = _ref.length; _i < _len; i = ++_i) {
                                    child = _ref[i];
                                    if (i === (children_length - 1)) {
                                        _iterateNode(node.children[i], null);
                                    } else {
                                        _iterateNode(node.children[i], node.children[i + 1]);
                                    }
                                }
                                if (node.is_open) {
                                    return _this.handleAfterOpenFolder(node, next_node, $element);
                                }
                            }
                        };
                    })(this);
                    return _iterateNode(this.tree, null);
                };

                VisibleNodeIterator.prototype.handleNode = function (node, next_node, $element) {
                };

                VisibleNodeIterator.prototype.handleOpenFolder = function (node, $element) {
                };

                VisibleNodeIterator.prototype.handleClosedFolder = function (node, next_node, $element) {
                };

                VisibleNodeIterator.prototype.handleAfterOpenFolder = function (node, next_node, $element) {
                };

                VisibleNodeIterator.prototype.handleFirstNode = function (node, $element) {
                };

                return VisibleNodeIterator;

            })();

            HitAreasGenerator = (function (_super) {
                __extends(HitAreasGenerator, _super);

                function HitAreasGenerator(tree, current_node, tree_bottom) {
                    HitAreasGenerator.__super__.constructor.call(this, tree);
                    this.current_node = current_node;
                    this.tree_bottom = tree_bottom;
                }

                HitAreasGenerator.prototype.generate = function () {
                    this.positions = [];
                    this.last_top = 0;
                    this.iterate();
                    return this.generateHitAreas(this.positions);
                };

                HitAreasGenerator.prototype.getTop = function ($element) {
                    return $element.offset().top;
                };

                HitAreasGenerator.prototype.addPosition = function (node, position, top) {
                    var area;
                    area = {
                        top: top,
                        node: node,
                        position: position
                    };
                    this.positions.push(area);
                    return this.last_top = top;
                };

                HitAreasGenerator.prototype.handleNode = function (node, next_node, $element) {
                    var top;
                    top = this.getTop($element);
                    if (node === this.current_node) {
                        this.addPosition(node, Position.NONE, top);
                    } else {
                        this.addPosition(node, Position.INSIDE, top);
                    }
                    if (next_node === this.current_node || node === this.current_node) {
                        return this.addPosition(node, Position.NONE, top);
                    } else {
                        return this.addPosition(node, Position.AFTER, top);
                    }
                };

                HitAreasGenerator.prototype.handleOpenFolder = function (node, $element) {
                    if (node === this.current_node) {
                        return false;
                    }
                    if (node.children[0] !== this.current_node) {
                        this.addPosition(node, Position.INSIDE, this.getTop($element));
                    }
                    return true;
                };

                HitAreasGenerator.prototype.handleClosedFolder = function (node, next_node, $element) {
                    var top;
                    top = this.getTop($element);
                    if (node === this.current_node) {
                        return this.addPosition(node, Position.NONE, top);
                    } else {
                        this.addPosition(node, Position.INSIDE, top);
                        if (next_node !== this.current_node) {
                            return this.addPosition(node, Position.AFTER, top);
                        }
                    }
                };

                HitAreasGenerator.prototype.handleFirstNode = function (node, $element) {
                    if (node !== this.current_node) {
                        return this.addPosition(node, Position.BEFORE, this.getTop($(node.element)));
                    }
                };

                HitAreasGenerator.prototype.handleAfterOpenFolder = function (node, next_node, $element) {
                    if (node === this.current_node.node || next_node === this.current_node.node) {
                        return this.addPosition(node, Position.NONE, this.last_top);
                    } else {
                        return this.addPosition(node, Position.AFTER, this.last_top);
                    }
                };

                HitAreasGenerator.prototype.generateHitAreas = function (positions) {
                    var group, hit_areas, position, previous_top, _i, _len;
                    previous_top = -1;
                    group = [];
                    hit_areas = [];
                    for (_i = 0, _len = positions.length; _i < _len; _i++) {
                        position = positions[_i];
                        if (position.top !== previous_top && group.length) {
                            if (group.length) {
                                this.generateHitAreasForGroup(hit_areas, group, previous_top, position.top);
                            }
                            previous_top = position.top;
                            group = [];
                        }
                        group.push(position);
                    }
                    this.generateHitAreasForGroup(hit_areas, group, previous_top, this.tree_bottom);
                    return hit_areas;
                };

                HitAreasGenerator.prototype.generateHitAreasForGroup = function (hit_areas, positions_in_group, top, bottom) {
                    var area_height, area_top, i, position, position_count;
                    position_count = Math.min(positions_in_group.length, 4);
                    area_height = Math.round((bottom - top) / position_count);
                    area_top = top;
                    i = 0;
                    while (i < position_count) {
                        position = positions_in_group[i];
                        hit_areas.push({
                            top: area_top,
                            bottom: area_top + area_height,
                            node: position.node,
                            position: position.position
                        });
                        area_top += area_height;
                        i += 1;
                    }
                    return null;
                };

                return HitAreasGenerator;

            })(VisibleNodeIterator);

            DragElement = (function () {
                function DragElement(node, offset_x, offset_y, $tree) {
                    this.offset_x = offset_x;
                    this.offset_y = offset_y;
                    this.$element = $("<span class=\"jqtree-title jqtree-dragging\">" + node.name + "</span>");
                    this.$element.css("position", "absolute");
                    $tree.append(this.$element);
                }

                DragElement.prototype.move = function (page_x, page_y) {
                    return this.$element.offset({
                        left: page_x - this.offset_x,
                        top: page_y - this.offset_y
                    });
                };

                DragElement.prototype.remove = function () {
                    return this.$element.remove();
                };

                return DragElement;

            })();

            module.exports = DragAndDropHandler;

        }).call(this);

    }, {"./node": 6}], 2: [function (require, module, exports) {
        (function () {
            var ElementsRenderer, NodeElement, html_escape, node_element, util;

            node_element = require('./node_element');

            NodeElement = node_element.NodeElement;

            util = require('./util');

            html_escape = util.html_escape;

            ElementsRenderer = (function () {
                function ElementsRenderer(tree_widget) {
                    this.tree_widget = tree_widget;
                    this.opened_icon_element = this.createButtonElement(tree_widget.options.openedIcon);
                    this.closed_icon_element = this.createButtonElement(tree_widget.options.closedIcon);
                }

                ElementsRenderer.prototype.render = function (from_node) {
                    if (from_node && from_node.parent) {
                        return this.renderFromNode(from_node);
                    } else {
                        return this.renderFromRoot();
                    }
                };

                ElementsRenderer.prototype.renderFromRoot = function () {
                    var $element;
                    $element = this.tree_widget.element;
                    $element.empty();
                    return this.createDomElements($element[0], this.tree_widget.tree.children, true, true);
                };

                ElementsRenderer.prototype.renderFromNode = function (node) {
                    var $previous_li, li, parent_node_element;
                    $previous_li = $(node.element);
                    parent_node_element = new NodeElement(node.parent, this.tree_widget);
                    li = this.createLi(node);
                    this.attachNodeData(node, li);
                    $previous_li.after(li);
                    $previous_li.remove();
                    if (node.children) {
                        return this.createDomElements(li, node.children, false, false);
                    }
                };

                ElementsRenderer.prototype.createDomElements = function (element, children, is_root_node, is_open) {
                    var child, li, ul, _i, _len;
                    ul = this.createUl(is_root_node);
                    element.appendChild(ul);
                    for (_i = 0, _len = children.length; _i < _len; _i++) {
                        child = children[_i];
                        li = this.createLi(child);
                        ul.appendChild(li);
                        this.attachNodeData(child, li);
                        if (child.hasChildren()) {
                            this.createDomElements(li, child.children, false, child.is_open);
                        }
                    }
                    return null;
                };

                ElementsRenderer.prototype.attachNodeData = function (node, li) {
                    node.element = li;
                    return $(li).data('node', node);
                };

                ElementsRenderer.prototype.createUl = function (is_root_node) {
                    var class_string, ul;
                    if (is_root_node) {
                        class_string = 'jqtree-tree';
                    } else {
                        class_string = '';
                    }
                    ul = document.createElement('ul');
                    ul.className = "jqtree_common " + class_string;
                    return ul;
                };

                ElementsRenderer.prototype.createLi = function (node) {
                    var li;
                    if (node.isFolder()) {
                        li = this.createFolderLi(node);
                    } else {
                        li = this.createNodeLi(node);
                    }
                    if (this.tree_widget.options.onCreateLi) {
                        this.tree_widget.options.onCreateLi(node, $(li));
                    }
                    return li;
                };

                ElementsRenderer.prototype.createFolderLi = function (node) {
                    var button_classes, button_link, div, escaped_name, folder_classes, icon_element, li, title_span;
                    button_classes = this.getButtonClasses(node);
                    folder_classes = this.getFolderClasses(node);
                    escaped_name = this.escapeIfNecessary(node.name);
                    if (node.is_open) {
                        icon_element = this.opened_icon_element;
                    } else {
                        icon_element = this.closed_icon_element;
                    }
                    li = document.createElement('li');
                    li.className = "jqtree_common " + folder_classes;
                    div = document.createElement('div');
                    div.className = "jqtree-element jqtree_common";
                    li.appendChild(div);
                    button_link = document.createElement('a');
                    button_link.className = "jqtree_common " + button_classes;
                    button_link.appendChild(icon_element.cloneNode(false));
                    div.appendChild(button_link);
                    title_span = document.createElement('span');
                    title_span.className = "jqtree_common jqtree-title jqtree-title-folder";
                    div.appendChild(title_span);
                    title_span.innerHTML = escaped_name;
                    return li;
                };

                ElementsRenderer.prototype.createNodeLi = function (node) {
                    var class_string, div, escaped_name, li, li_classes, title_span;
                    li_classes = ['jqtree_common'];
                    if (this.tree_widget.select_node_handler && this.tree_widget.select_node_handler.isNodeSelected(node)) {
                        li_classes.push('jqtree-selected');
                    }
                    class_string = li_classes.join(' ');
                    escaped_name = this.escapeIfNecessary(node.name);
                    li = document.createElement('li');
                    li.className = class_string;
                    div = document.createElement('div');
                    div.className = "jqtree-element jqtree_common";
                    li.appendChild(div);
                    title_span = document.createElement('span');
                    title_span.className = "jqtree-title jqtree_common";
                    title_span.innerHTML = escaped_name;
                    div.appendChild(title_span);
                    return li;
                };

                ElementsRenderer.prototype.getButtonClasses = function (node) {
                    var classes;
                    classes = ['jqtree-toggler'];
                    if (!node.is_open) {
                        classes.push('jqtree-closed');
                    }
                    return classes.join(' ');
                };

                ElementsRenderer.prototype.getFolderClasses = function (node) {
                    var classes;
                    classes = ['jqtree-folder'];
                    if (!node.is_open) {
                        classes.push('jqtree-closed');
                    }
                    if (this.tree_widget.select_node_handler && this.tree_widget.select_node_handler.isNodeSelected(node)) {
                        classes.push('jqtree-selected');
                    }
                    if (node.is_loading) {
                        classes.push('jqtree-loading');
                    }
                    return classes.join(' ');
                };

                ElementsRenderer.prototype.escapeIfNecessary = function (value) {
                    if (this.tree_widget.options.autoEscape) {
                        return html_escape(value);
                    } else {
                        return value;
                    }
                };

                ElementsRenderer.prototype.createButtonElement = function (value) {
                    var div;
                    if (typeof value === 'string') {
                        div = document.createElement('div');
                        div.innerHTML = value;
                        return document.createTextNode(div.innerHTML);
                    } else {
                        return $(value)[0];
                    }
                };

                return ElementsRenderer;

            })();

            module.exports = ElementsRenderer;

        }).call(this);

    }, {"./node_element": 7, "./util": 12}], 3: [function (require, module, exports) {

        /*
Copyright 2013 Marco Braak

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

        (function () {
            var DragAndDropHandler, ElementsRenderer, FolderElement, JqTreeWidget, KeyHandler, MouseWidget, Node,
                NodeElement, Position, SaveStateHandler, ScrollHandler, SelectNodeHandler, SimpleWidget, node,
                node_element, __version__,
                __hasProp = {}.hasOwnProperty,
                __extends = function (child, parent) {
                    for (var key in parent) {
                        if (__hasProp.call(parent, key)) child[key] = parent[key];
                    }

                    function ctor() {
                        this.constructor = child;
                    }

                    ctor.prototype = parent.prototype;
                    child.prototype = new ctor();
                    child.__super__ = parent.prototype;
                    return child;
                };

            __version__ = require('./version');

            DragAndDropHandler = require('./drag_and_drop_handler');

            ElementsRenderer = require('./elements_renderer');

            KeyHandler = require('./key_handler');

            MouseWidget = require('./mouse.widget');

            SaveStateHandler = require('./save_state_handler');

            ScrollHandler = require('./scroll_handler');

            SelectNodeHandler = require('./select_node_handler');

            SimpleWidget = require('./simple.widget');

            node = require('./node');

            Node = node.Node;

            Position = node.Position;

            node_element = require('./node_element');

            NodeElement = node_element.NodeElement;

            FolderElement = node_element.FolderElement;

            JqTreeWidget = (function (_super) {
                __extends(JqTreeWidget, _super);

                function JqTreeWidget() {
                    return JqTreeWidget.__super__.constructor.apply(this, arguments);
                }

                JqTreeWidget.prototype.defaults = {
                    autoOpen: false,
                    saveState: false,
                    dragAndDrop: false,
                    selectable: true,
                    useContextMenu: true,
                    onCanSelectNode: null,
                    onSetStateFromStorage: null,
                    onGetStateFromStorage: null,
                    onCreateLi: null,
                    onIsMoveHandle: null,
                    onCanMove: null,
                    onCanMoveTo: null,
                    onLoadFailed: null,
                    autoEscape: true,
                    dataUrl: null,
                    closedIcon: '&#x25ba;',
                    openedIcon: '&#x25bc;',
                    slide: true,
                    nodeClass: Node,
                    dataFilter: null,
                    keyboardSupport: true,
                    openFolderDelay: 500
                };

                JqTreeWidget.prototype.toggle = function (node, slide) {
                    if (slide == null) {
                        slide = null;
                    }
                    if (slide === null) {
                        slide = this.options.slide;
                    }
                    if (node.is_open) {
                        return this.closeNode(node, slide);
                    } else {
                        return this.openNode(node, slide);
                    }
                };

                JqTreeWidget.prototype.getTree = function () {
                    return this.tree;
                };

                JqTreeWidget.prototype.selectNode = function (node) {
                    return this._selectNode(node, false);
                };

                JqTreeWidget.prototype._selectNode = function (node, must_toggle) {
                    var canSelect, deselected_node, openParents, saveState;
                    if (must_toggle == null) {
                        must_toggle = false;
                    }
                    if (!this.select_node_handler) {
                        return;
                    }
                    canSelect = (function (_this) {
                        return function () {
                            if (_this.options.onCanSelectNode) {
                                return _this.options.selectable && _this.options.onCanSelectNode(node);
                            } else {
                                return _this.options.selectable;
                            }
                        };
                    })(this);
                    openParents = (function (_this) {
                        return function () {
                            var parent;
                            parent = node.parent;
                            if (parent && parent.parent && !parent.is_open) {
                                return _this.openNode(parent, false);
                            }
                        };
                    })(this);
                    saveState = (function (_this) {
                        return function () {
                            if (_this.options.saveState) {
                                return _this.save_state_handler.saveState();
                            }
                        };
                    })(this);
                    if (!node) {
                        this._deselectCurrentNode();
                        saveState();
                        return;
                    }
                    if (!canSelect()) {
                        return;
                    }
                    if (this.select_node_handler.isNodeSelected(node)) {
                        if (must_toggle) {
                            this._deselectCurrentNode();
                            this._triggerEvent('tree.select', {
                                node: null,
                                previous_node: node
                            });
                        }
                    } else {
                        deselected_node = this.getSelectedNode();
                        this._deselectCurrentNode();
                        this.addToSelection(node);
                        this._triggerEvent('tree.select', {
                            node: node,
                            deselected_node: deselected_node
                        });
                        openParents();
                    }
                    return saveState();
                };

                JqTreeWidget.prototype.getSelectedNode = function () {
                    if (this.select_node_handler) {
                        return this.select_node_handler.getSelectedNode();
                    } else {
                        return null;
                    }
                };

                JqTreeWidget.prototype.toJson = function () {
                    return JSON.stringify(this.tree.getData());
                };

                JqTreeWidget.prototype.loadData = function (data, parent_node) {
                    return this._loadData(data, parent_node);
                };

                JqTreeWidget.prototype.loadDataFromUrl = function (url, parent_node, on_finished) {
                    if ($.type(url) !== 'string') {
                        on_finished = parent_node;
                        parent_node = url;
                        url = null;
                    }
                    return this._loadDataFromUrl(url, parent_node, on_finished);
                };

                JqTreeWidget.prototype.reload = function () {
                    return this.loadDataFromUrl();
                };

                JqTreeWidget.prototype._loadDataFromUrl = function (url_info, parent_node, on_finished) {
                    var $el, addLoadingClass, handeLoadData, loadDataFromUrlInfo, parseUrlInfo, removeLoadingClass;
                    $el = null;
                    addLoadingClass = (function (_this) {
                        return function () {
                            if (parent_node) {
                                $el = $(parent_node.element);
                            } else {
                                $el = _this.element;
                            }
                            return $el.addClass('jqtree-loading');
                        };
                    })(this);
                    removeLoadingClass = function () {
                        if ($el) {
                            return $el.removeClass('jqtree-loading');
                        }
                    };
                    parseUrlInfo = function () {
                        if ($.type(url_info) === 'string') {
                            url_info = {
                                url: url_info
                            };
                        }
                        if (!url_info.method) {
                            return url_info.method = 'get';
                        }
                    };
                    handeLoadData = (function (_this) {
                        return function (data) {
                            removeLoadingClass();
                            _this._loadData(data, parent_node);
                            if (on_finished && $.isFunction(on_finished)) {
                                return on_finished();
                            }
                        };
                    })(this);
                    loadDataFromUrlInfo = (function (_this) {
                        return function () {
                            parseUrlInfo();
                            return $.ajax({
                                url: url_info.url,
                                data: url_info.data,
                                type: url_info.method.toUpperCase(),
                                cache: false,
                                dataType: 'json',
                                success: function (response) {
                                    var data;
                                    if ($.isArray(response) || typeof response === 'object') {
                                        data = response;
                                    } else {
                                        data = $.parseJSON(response);
                                    }
                                    if (_this.options.dataFilter) {
                                        data = _this.options.dataFilter(data);
                                    }
                                    return handeLoadData(data);
                                },
                                error: function (response) {
                                    removeLoadingClass();
                                    if (_this.options.onLoadFailed) {
                                        return _this.options.onLoadFailed(response);
                                    }
                                }
                            });
                        };
                    })(this);
                    if (!url_info) {
                        url_info = this._getDataUrlInfo(parent_node);
                    }
                    addLoadingClass();
                    if (!url_info) {
                        removeLoadingClass();
                    } else if ($.isArray(url_info)) {
                        handeLoadData(url_info);
                    } else {
                        return loadDataFromUrlInfo();
                    }
                };

                JqTreeWidget.prototype._loadData = function (data, parent_node) {
                    var deselectNodes, loadSubtree;
                    if (parent_node == null) {
                        parent_node = null;
                    }
                    deselectNodes = (function (_this) {
                        return function () {
                            var n, selected_nodes_under_parent, _i, _len;
                            if (_this.select_node_handler) {
                                selected_nodes_under_parent = _this.select_node_handler.getSelectedNodesUnder(parent_node);
                                for (_i = 0, _len = selected_nodes_under_parent.length; _i < _len; _i++) {
                                    n = selected_nodes_under_parent[_i];
                                    _this.select_node_handler.removeFromSelection(n);
                                }
                            }
                            return null;
                        };
                    })(this);
                    loadSubtree = (function (_this) {
                        return function () {
                            parent_node.loadFromData(data);
                            parent_node.load_on_demand = false;
                            parent_node.is_loading = false;
                            return _this._refreshElements(parent_node);
                        };
                    })(this);
                    if (!data) {
                        return;
                    }
                    this._triggerEvent('tree.load_data', {
                        tree_data: data
                    });
                    if (!parent_node) {
                        this._initTree(data);
                    } else {
                        deselectNodes();
                        loadSubtree();
                    }
                    if (this.isDragging()) {
                        return this.dnd_handler.refresh();
                    }
                };

                JqTreeWidget.prototype.getNodeById = function (node_id) {
                    return this.tree.getNodeById(node_id);
                };

                JqTreeWidget.prototype.getNodeByName = function (name) {
                    return this.tree.getNodeByName(name);
                };

                JqTreeWidget.prototype.getNodesByProperty = function (key, value) {
                    return this.tree.getNodesByProperty(key, value);
                };

                JqTreeWidget.prototype.openNode = function (node, slide) {
                    if (slide == null) {
                        slide = null;
                    }
                    if (slide === null) {
                        slide = this.options.slide;
                    }
                    return this._openNode(node, slide);
                };

                JqTreeWidget.prototype._openNode = function (node, slide, on_finished) {
                    var doOpenNode, parent;
                    if (slide == null) {
                        slide = true;
                    }
                    doOpenNode = (function (_this) {
                        return function (_node, _slide, _on_finished) {
                            var folder_element;
                            folder_element = new FolderElement(_node, _this);
                            return folder_element.open(_on_finished, _slide);
                        };
                    })(this);
                    if (node.isFolder()) {
                        if (node.load_on_demand) {
                            return this._loadFolderOnDemand(node, slide, on_finished);
                        } else {
                            parent = node.parent;
                            while (parent) {
                                if (parent.parent) {
                                    doOpenNode(parent, false, null);
                                }
                                parent = parent.parent;
                            }
                            doOpenNode(node, slide, on_finished);
                            return this._saveState();
                        }
                    }
                };

                JqTreeWidget.prototype._loadFolderOnDemand = function (node, slide, on_finished) {
                    if (slide == null) {
                        slide = true;
                    }
                    node.is_loading = true;
                    return this._loadDataFromUrl(null, node, (function (_this) {
                        return function () {
                            return _this._openNode(node, slide, on_finished);
                        };
                    })(this));
                };

                JqTreeWidget.prototype.closeNode = function (node, slide) {
                    if (slide == null) {
                        slide = null;
                    }
                    if (slide === null) {
                        slide = this.options.slide;
                    }
                    if (node.isFolder()) {
                        new FolderElement(node, this).close(slide);
                        return this._saveState();
                    }
                };

                JqTreeWidget.prototype.isDragging = function () {
                    if (this.dnd_handler) {
                        return this.dnd_handler.is_dragging;
                    } else {
                        return false;
                    }
                };

                JqTreeWidget.prototype.refreshHitAreas = function () {
                    return this.dnd_handler.refresh();
                };

                JqTreeWidget.prototype.addNodeAfter = function (new_node_info, existing_node) {
                    var new_node;
                    new_node = existing_node.addAfter(new_node_info);
                    this._refreshElements(existing_node.parent);
                    return new_node;
                };

                JqTreeWidget.prototype.addNodeBefore = function (new_node_info, existing_node) {
                    var new_node;
                    new_node = existing_node.addBefore(new_node_info);
                    this._refreshElements(existing_node.parent);
                    return new_node;
                };

                JqTreeWidget.prototype.addParentNode = function (new_node_info, existing_node) {
                    var new_node;
                    new_node = existing_node.addParent(new_node_info);
                    this._refreshElements(new_node.parent);
                    return new_node;
                };

                JqTreeWidget.prototype.removeNode = function (node) {
                    var parent;
                    parent = node.parent;
                    if (parent) {
                        this.select_node_handler.removeFromSelection(node, true);
                        node.remove();
                        return this._refreshElements(parent);
                    }
                };

                JqTreeWidget.prototype.appendNode = function (new_node_info, parent_node) {
                    parent_node = parent_node || this.tree;
                    node = parent_node.append(new_node_info);
                    this._refreshElements(parent_node);
                    return node;
                };

                JqTreeWidget.prototype.prependNode = function (new_node_info, parent_node) {
                    if (!parent_node) {
                        parent_node = this.tree;
                    }
                    node = parent_node.prepend(new_node_info);
                    this._refreshElements(parent_node);
                    return node;
                };

                JqTreeWidget.prototype.updateNode = function (node, data) {
                    var id_is_changed;
                    id_is_changed = data.id && data.id !== node.id;
                    if (id_is_changed) {
                        this.tree.removeNodeFromIndex(node);
                    }
                    node.setData(data);
                    if (id_is_changed) {
                        this.tree.addNodeToIndex(node);
                    }
                    this.renderer.renderFromNode(node);
                    return this._selectCurrentNode();
                };

                JqTreeWidget.prototype.moveNode = function (node, target_node, position) {
                    var position_index;
                    position_index = Position.nameToIndex(position);
                    this.tree.moveNode(node, target_node, position_index);
                    return this._refreshElements();
                };

                JqTreeWidget.prototype.getStateFromStorage = function () {
                    return this.save_state_handler.getStateFromStorage();
                };

                JqTreeWidget.prototype.addToSelection = function (node) {
                    if (node) {
                        this.select_node_handler.addToSelection(node);
                        this._getNodeElementForNode(node).select();
                        return this._saveState();
                    }
                };

                JqTreeWidget.prototype.getSelectedNodes = function () {
                    return this.select_node_handler.getSelectedNodes();
                };

                JqTreeWidget.prototype.isNodeSelected = function (node) {
                    return this.select_node_handler.isNodeSelected(node);
                };

                JqTreeWidget.prototype.removeFromSelection = function (node) {
                    this.select_node_handler.removeFromSelection(node);
                    this._getNodeElementForNode(node).deselect();
                    return this._saveState();
                };

                JqTreeWidget.prototype.scrollToNode = function (node) {
                    var $element, top;
                    $element = $(node.element);
                    top = $element.offset().top - this.$el.offset().top;
                    return this.scroll_handler.scrollTo(top);
                };

                JqTreeWidget.prototype.getState = function () {
                    return this.save_state_handler.getState();
                };

                JqTreeWidget.prototype.setState = function (state) {
                    this.save_state_handler.setInitialState(state);
                    return this._refreshElements();
                };

                JqTreeWidget.prototype.setOption = function (option, value) {
                    return this.options[option] = value;
                };

                JqTreeWidget.prototype.moveDown = function () {
                    if (this.key_handler) {
                        return this.key_handler.moveDown();
                    }
                };

                JqTreeWidget.prototype.moveUp = function () {
                    if (this.key_handler) {
                        return this.key_handler.moveUp();
                    }
                };

                JqTreeWidget.prototype.getVersion = function () {
                    return __version__;
                };

                JqTreeWidget.prototype._init = function () {
                    JqTreeWidget.__super__._init.call(this);
                    this.element = this.$el;
                    this.mouse_delay = 300;
                    this.is_initialized = false;
                    this.renderer = new ElementsRenderer(this);
                    if (SaveStateHandler != null) {
                        this.save_state_handler = new SaveStateHandler(this);
                    } else {
                        this.options.saveState = false;
                    }
                    if (SelectNodeHandler != null) {
                        this.select_node_handler = new SelectNodeHandler(this);
                    }
                    if (DragAndDropHandler != null) {
                        this.dnd_handler = new DragAndDropHandler(this);
                    } else {
                        this.options.dragAndDrop = false;
                    }
                    if (ScrollHandler != null) {
                        this.scroll_handler = new ScrollHandler(this);
                    }
                    if ((KeyHandler != null) && (SelectNodeHandler != null)) {
                        this.key_handler = new KeyHandler(this);
                    }
                    this._initData();
                    this.element.click($.proxy(this._click, this));
                    this.element.dblclick($.proxy(this._dblclick, this));
                    if (this.options.useContextMenu) {
                        return this.element.bind('contextmenu', $.proxy(this._contextmenu, this));
                    }
                };

                JqTreeWidget.prototype._deinit = function () {
                    this.element.empty();
                    this.element.unbind();
                    if (this.key_handler) {
                        this.key_handler.deinit();
                    }
                    this.tree = null;
                    return JqTreeWidget.__super__._deinit.call(this);
                };

                JqTreeWidget.prototype._initData = function () {
                    if (this.options.data) {
                        return this._loadData(this.options.data);
                    } else {
                        return this._loadDataFromUrl(this._getDataUrlInfo());
                    }
                };

                JqTreeWidget.prototype._getDataUrlInfo = function (node) {
                    var data_url, getUrlFromString;
                    data_url = this.options.dataUrl || this.element.data('url');
                    getUrlFromString = (function (_this) {
                        return function () {
                            var data, selected_node_id, url_info;
                            url_info = {
                                url: data_url
                            };
                            if (node && node.id) {
                                data = {
                                    node: node.id
                                };
                                url_info['data'] = data;
                            } else {
                                selected_node_id = _this._getNodeIdToBeSelected();
                                if (selected_node_id) {
                                    data = {
                                        selected_node: selected_node_id
                                    };
                                    url_info['data'] = data;
                                }
                            }
                            return url_info;
                        };
                    })(this);
                    if ($.isFunction(data_url)) {
                        return data_url(node);
                    } else if ($.type(data_url) === 'string') {
                        return getUrlFromString();
                    } else {
                        return data_url;
                    }
                };

                JqTreeWidget.prototype._getNodeIdToBeSelected = function () {
                    if (this.options.saveState) {
                        return this.save_state_handler.getNodeIdToBeSelected();
                    } else {
                        return null;
                    }
                };

                JqTreeWidget.prototype._initTree = function (data) {
                    var must_load_on_demand;
                    this.tree = new this.options.nodeClass(null, true, this.options.nodeClass);
                    if (this.select_node_handler) {
                        this.select_node_handler.clear();
                    }
                    this.tree.loadFromData(data);
                    must_load_on_demand = this._setInitialState();
                    this._refreshElements();
                    if (must_load_on_demand) {
                        this._setInitialStateOnDemand();
                    }
                    if (!this.is_initialized) {
                        this.is_initialized = true;
                        return this._triggerEvent('tree.init');
                    }
                };

                JqTreeWidget.prototype._setInitialState = function () {
                    var autoOpenNodes, is_restored, must_load_on_demand, restoreState, _ref;
                    restoreState = (function (_this) {
                        return function () {
                            var must_load_on_demand, state;
                            if (!(_this.options.saveState && _this.save_state_handler)) {
                                return [false, false];
                            } else {
                                state = _this.save_state_handler.getStateFromStorage();
                                if (!state) {
                                    return [false, false];
                                } else {
                                    must_load_on_demand = _this.save_state_handler.setInitialState(state);
                                    return [true, must_load_on_demand];
                                }
                            }
                        };
                    })(this);
                    autoOpenNodes = (function (_this) {
                        return function () {
                            var max_level, must_load_on_demand;
                            if (_this.options.autoOpen === false) {
                                return false;
                            }
                            max_level = _this._getAutoOpenMaxLevel();
                            must_load_on_demand = false;
                            _this.tree.iterate(function (node, level) {
                                if (node.load_on_demand) {
                                    must_load_on_demand = true;
                                    return false;
                                } else if (!node.hasChildren()) {
                                    return false;
                                } else {
                                    node.is_open = true;
                                    return level !== max_level;
                                }
                            });
                            return must_load_on_demand;
                        };
                    })(this);
                    _ref = restoreState(), is_restored = _ref[0], must_load_on_demand = _ref[1];
                    if (!is_restored) {
                        must_load_on_demand = autoOpenNodes();
                    }
                    return must_load_on_demand;
                };

                JqTreeWidget.prototype._setInitialStateOnDemand = function () {
                    var autoOpenNodes, restoreState;
                    restoreState = (function (_this) {
                        return function () {
                            var state;
                            if (!(_this.options.saveState && _this.save_state_handler)) {
                                return false;
                            } else {
                                state = _this.save_state_handler.getStateFromStorage();
                                if (!state) {
                                    return false;
                                } else {
                                    _this.save_state_handler.setInitialStateOnDemand(state);
                                    return true;
                                }
                            }
                        };
                    })(this);
                    autoOpenNodes = (function (_this) {
                        return function () {
                            var loadAndOpenNode, loading_ids, max_level, openNodes;
                            max_level = _this._getAutoOpenMaxLevel();
                            loading_ids = [];
                            loadAndOpenNode = function (node) {
                                return _this._openNode(node, false, openNodes);
                            };
                            openNodes = function () {
                                return _this.tree.iterate(function (node, level) {
                                    if (node.load_on_demand) {
                                        if (!node.is_loading) {
                                            loadAndOpenNode(node);
                                        }
                                        return false;
                                    } else {
                                        _this._openNode(node, false);
                                        return level !== max_level;
                                    }
                                });
                            };
                            return openNodes();
                        };
                    })(this);
                    if (!restoreState()) {
                        return autoOpenNodes();
                    }
                };

                JqTreeWidget.prototype._getAutoOpenMaxLevel = function () {
                    if (this.options.autoOpen === true) {
                        return -1;
                    } else {
                        return parseInt(this.options.autoOpen);
                    }
                };


                /*
    Redraw the tree or part of the tree.
     * from_node: redraw this subtree
     */

                JqTreeWidget.prototype._refreshElements = function (from_node) {
                    if (from_node == null) {
                        from_node = null;
                    }
                    this.renderer.render(from_node);
                    return this._triggerEvent('tree.refresh');
                };

                JqTreeWidget.prototype._click = function (e) {
                    var click_target, event;
                    click_target = this._getClickTarget(e.target);
                    if (click_target) {
                        if (click_target.type === 'button') {
                            this.toggle(click_target.node, this.options.slide);
                            e.preventDefault();
                            return e.stopPropagation();
                        } else if (click_target.type === 'label') {
                            node = click_target.node;
                            event = this._triggerEvent('tree.click', {
                                node: node,
                                click_event: e
                            });
                            if (!event.isDefaultPrevented()) {
                                return this._selectNode(node, true);
                            }
                        }
                    }
                };

                JqTreeWidget.prototype._dblclick = function (e) {
                    var click_target;
                    click_target = this._getClickTarget(e.target);
                    if (click_target && click_target.type === 'label') {
                        return this._triggerEvent('tree.dblclick', {
                            node: click_target.node,
                            click_event: e
                        });
                    }
                };

                JqTreeWidget.prototype._getClickTarget = function (element) {
                    var $button, $el, $target;
                    $target = $(element);
                    $button = $target.closest('.jqtree-toggler');
                    if ($button.length) {
                        node = this._getNode($button);
                        if (node) {
                            return {
                                type: 'button',
                                node: node
                            };
                        }
                    } else {
                        $el = $target.closest('.jqtree-element');
                        if ($el.length) {
                            node = this._getNode($el);
                            if (node) {
                                return {
                                    type: 'label',
                                    node: node
                                };
                            }
                        }
                    }
                    return null;
                };

                JqTreeWidget.prototype._getNode = function ($element) {
                    var $li;
                    $li = $element.closest('li.jqtree_common');
                    if ($li.length === 0) {
                        return null;
                    } else {
                        return $li.data('node');
                    }
                };

                JqTreeWidget.prototype._getNodeElementForNode = function (node) {
                    if (node.isFolder()) {
                        return new FolderElement(node, this);
                    } else {
                        return new NodeElement(node, this);
                    }
                };

                JqTreeWidget.prototype._getNodeElement = function ($element) {
                    node = this._getNode($element);
                    if (node) {
                        return this._getNodeElementForNode(node);
                    } else {
                        return null;
                    }
                };

                JqTreeWidget.prototype._contextmenu = function (e) {
                    var $div;
                    $div = $(e.target).closest('ul.jqtree-tree .jqtree-element');
                    if ($div.length) {
                        node = this._getNode($div);
                        if (node) {
                            e.preventDefault();
                            e.stopPropagation();
                            this._triggerEvent('tree.contextmenu', {
                                node: node,
                                click_event: e
                            });
                            return false;
                        }
                    }
                };

                JqTreeWidget.prototype._saveState = function () {
                    if (this.options.saveState) {
                        return this.save_state_handler.saveState();
                    }
                };

                JqTreeWidget.prototype._mouseCapture = function (position_info) {
                    if (this.options.dragAndDrop) {
                        return this.dnd_handler.mouseCapture(position_info);
                    } else {
                        return false;
                    }
                };

                JqTreeWidget.prototype._mouseStart = function (position_info) {
                    if (this.options.dragAndDrop) {
                        return this.dnd_handler.mouseStart(position_info);
                    } else {
                        return false;
                    }
                };

                JqTreeWidget.prototype._mouseDrag = function (position_info) {
                    var result;
                    if (this.options.dragAndDrop) {
                        result = this.dnd_handler.mouseDrag(position_info);
                        if (this.scroll_handler) {
                            this.scroll_handler.checkScrolling();
                        }
                        return result;
                    } else {
                        return false;
                    }
                };

                JqTreeWidget.prototype._mouseStop = function (position_info) {
                    if (this.options.dragAndDrop) {
                        return this.dnd_handler.mouseStop(position_info);
                    } else {
                        return false;
                    }
                };

                JqTreeWidget.prototype._triggerEvent = function (event_name, values) {
                    var event;
                    event = $.Event(event_name);
                    $.extend(event, values);
                    this.element.trigger(event);
                    return event;
                };

                JqTreeWidget.prototype.testGenerateHitAreas = function (moving_node) {
                    this.dnd_handler.current_item = this._getNodeElementForNode(moving_node);
                    this.dnd_handler.generateHitAreas();
                    return this.dnd_handler.hit_areas;
                };

                JqTreeWidget.prototype._selectCurrentNode = function () {
                    node = this.getSelectedNode();
                    if (node) {
                        node_element = this._getNodeElementForNode(node);
                        if (node_element) {
                            return node_element.select();
                        }
                    }
                };

                JqTreeWidget.prototype._deselectCurrentNode = function () {
                    node = this.getSelectedNode();
                    if (node) {
                        return this.removeFromSelection(node);
                    }
                };

                return JqTreeWidget;

            })(MouseWidget);

            SimpleWidget.register(JqTreeWidget, 'tree');

        }).call(this);

    }, {
        "./drag_and_drop_handler": 1,
        "./elements_renderer": 2,
        "./key_handler": 4,
        "./mouse.widget": 5,
        "./node": 6,
        "./node_element": 7,
        "./save_state_handler": 8,
        "./scroll_handler": 9,
        "./select_node_handler": 10,
        "./simple.widget": 11,
        "./version": 13
    }], 4: [function (require, module, exports) {
        (function () {
            var KeyHandler,
                __bind = function (fn, me) {
                    return function () {
                        return fn.apply(me, arguments);
                    };
                };

            KeyHandler = (function () {
                var DOWN, LEFT, RIGHT, UP;

                LEFT = 37;

                UP = 38;

                RIGHT = 39;

                DOWN = 40;

                function KeyHandler(tree_widget) {
                    this.selectNode = __bind(this.selectNode, this);
                    this.tree_widget = tree_widget;
                    if (tree_widget.options.keyboardSupport) {
                        $(document).bind('keydown.jqtree', $.proxy(this.handleKeyDown, this));
                    }
                }

                KeyHandler.prototype.deinit = function () {
                    return $(document).unbind('keydown.jqtree');
                };

                KeyHandler.prototype.moveDown = function () {
                    var node;
                    node = this.tree_widget.getSelectedNode();
                    if (node) {
                        return this.selectNode(node.getNextNode());
                    } else {
                        return false;
                    }
                };

                KeyHandler.prototype.moveUp = function () {
                    var node;
                    node = this.tree_widget.getSelectedNode();
                    if (node) {
                        return this.selectNode(node.getPreviousNode());
                    } else {
                        return false;
                    }
                };

                KeyHandler.prototype.moveRight = function () {
                    var node;
                    node = this.tree_widget.getSelectedNode();
                    if (node && node.isFolder() && !node.is_open) {
                        this.tree_widget.openNode(node);
                        return false;
                    } else {
                        return true;
                    }
                };

                KeyHandler.prototype.moveLeft = function () {
                    var node;
                    node = this.tree_widget.getSelectedNode();
                    if (node && node.isFolder() && node.is_open) {
                        this.tree_widget.closeNode(node);
                        return false;
                    } else {
                        return true;
                    }
                };

                KeyHandler.prototype.handleKeyDown = function (e) {
                    var key;
                    if (!this.tree_widget.options.keyboardSupport) {
                        return true;
                    }
                    if ($(document.activeElement).is('textarea,input,select')) {
                        return true;
                    }
                    if (!this.tree_widget.getSelectedNode()) {
                        return true;
                    }
                    key = e.which;
                    switch (key) {
                        case DOWN:
                            return this.moveDown();
                        case UP:
                            return this.moveUp();
                        case RIGHT:
                            return this.moveRight();
                        case LEFT:
                            return this.moveLeft();
                    }
                    return true;
                };

                KeyHandler.prototype.selectNode = function (node) {
                    if (!node) {
                        return true;
                    } else {
                        this.tree_widget.selectNode(node);
                        if (this.tree_widget.scroll_handler && (!this.tree_widget.scroll_handler.isScrolledIntoView($(node.element).find('.jqtree-element')))) {
                            this.tree_widget.scrollToNode(node);
                        }
                        return false;
                    }
                };

                return KeyHandler;

            })();

            module.exports = KeyHandler;

        }).call(this);

    }, {}], 5: [function (require, module, exports) {

        /*
This widget does the same a the mouse widget in jqueryui.
 */

        (function () {
            var MouseWidget, SimpleWidget,
                __hasProp = {}.hasOwnProperty,
                __extends = function (child, parent) {
                    for (var key in parent) {
                        if (__hasProp.call(parent, key)) child[key] = parent[key];
                    }

                    function ctor() {
                        this.constructor = child;
                    }

                    ctor.prototype = parent.prototype;
                    child.prototype = new ctor();
                    child.__super__ = parent.prototype;
                    return child;
                };

            SimpleWidget = require('./simple.widget');

            MouseWidget = (function (_super) {
                __extends(MouseWidget, _super);

                function MouseWidget() {
                    return MouseWidget.__super__.constructor.apply(this, arguments);
                }

                MouseWidget.is_mouse_handled = false;

                MouseWidget.prototype._init = function () {
                    this.$el.bind('mousedown.mousewidget', $.proxy(this._mouseDown, this));
                    this.$el.bind('touchstart.mousewidget', $.proxy(this._touchStart, this));
                    this.is_mouse_started = false;
                    this.mouse_delay = 0;
                    this._mouse_delay_timer = null;
                    this._is_mouse_delay_met = true;
                    return this.mouse_down_info = null;
                };

                MouseWidget.prototype._deinit = function () {
                    var $document;
                    this.$el.unbind('mousedown.mousewidget');
                    this.$el.unbind('touchstart.mousewidget');
                    $document = $(document);
                    $document.unbind('mousemove.mousewidget');
                    return $document.unbind('mouseup.mousewidget');
                };

                MouseWidget.prototype._mouseDown = function (e) {
                    var result;
                    if (e.which !== 1) {
                        return;
                    }
                    result = this._handleMouseDown(e, this._getPositionInfo(e));
                    if (result) {
                        e.preventDefault();
                    }
                    return result;
                };

                MouseWidget.prototype._handleMouseDown = function (e, position_info) {
                    if (MouseWidget.is_mouse_handled) {
                        return;
                    }
                    if (this.is_mouse_started) {
                        this._handleMouseUp(position_info);
                    }
                    this.mouse_down_info = position_info;
                    if (!this._mouseCapture(position_info)) {
                        return;
                    }
                    this._handleStartMouse();
                    this.is_mouse_handled = true;
                    return true;
                };

                MouseWidget.prototype._handleStartMouse = function () {
                    var $document;
                    $document = $(document);
                    $document.bind('mousemove.mousewidget', $.proxy(this._mouseMove, this));
                    $document.bind('touchmove.mousewidget', $.proxy(this._touchMove, this));
                    $document.bind('mouseup.mousewidget', $.proxy(this._mouseUp, this));
                    $document.bind('touchend.mousewidget', $.proxy(this._touchEnd, this));
                    if (this.mouse_delay) {
                        return this._startMouseDelayTimer();
                    }
                };

                MouseWidget.prototype._startMouseDelayTimer = function () {
                    if (this._mouse_delay_timer) {
                        clearTimeout(this._mouse_delay_timer);
                    }
                    this._mouse_delay_timer = setTimeout((function (_this) {
                        return function () {
                            return _this._is_mouse_delay_met = true;
                        };
                    })(this), this.mouse_delay);
                    return this._is_mouse_delay_met = false;
                };

                MouseWidget.prototype._mouseMove = function (e) {
                    return this._handleMouseMove(e, this._getPositionInfo(e));
                };

                MouseWidget.prototype._handleMouseMove = function (e, position_info) {
                    if (this.is_mouse_started) {
                        this._mouseDrag(position_info);
                        return e.preventDefault();
                    }
                    if (this.mouse_delay && !this._is_mouse_delay_met) {
                        return true;
                    }
                    this.is_mouse_started = this._mouseStart(this.mouse_down_info) !== false;
                    if (this.is_mouse_started) {
                        this._mouseDrag(position_info);
                    } else {
                        this._handleMouseUp(position_info);
                    }
                    return !this.is_mouse_started;
                };

                MouseWidget.prototype._getPositionInfo = function (e) {
                    return {
                        page_x: e.pageX,
                        page_y: e.pageY,
                        target: e.target,
                        original_event: e
                    };
                };

                MouseWidget.prototype._mouseUp = function (e) {
                    return this._handleMouseUp(this._getPositionInfo(e));
                };

                MouseWidget.prototype._handleMouseUp = function (position_info) {
                    var $document;
                    $document = $(document);
                    $document.unbind('mousemove.mousewidget');
                    $document.unbind('touchmove.mousewidget');
                    $document.unbind('mouseup.mousewidget');
                    $document.unbind('touchend.mousewidget');
                    if (this.is_mouse_started) {
                        this.is_mouse_started = false;
                        this._mouseStop(position_info);
                    }
                };

                MouseWidget.prototype._mouseCapture = function (position_info) {
                    return true;
                };

                MouseWidget.prototype._mouseStart = function (position_info) {
                    return null;
                };

                MouseWidget.prototype._mouseDrag = function (position_info) {
                    return null;
                };

                MouseWidget.prototype._mouseStop = function (position_info) {
                    return null;
                };

                MouseWidget.prototype.setMouseDelay = function (mouse_delay) {
                    return this.mouse_delay = mouse_delay;
                };

                MouseWidget.prototype._touchStart = function (e) {
                    var touch;
                    if (e.originalEvent.touches.length > 1) {
                        return;
                    }
                    touch = e.originalEvent.changedTouches[0];
                    return this._handleMouseDown(e, this._getPositionInfo(touch));
                };

                MouseWidget.prototype._touchMove = function (e) {
                    var touch;
                    if (e.originalEvent.touches.length > 1) {
                        return;
                    }
                    touch = e.originalEvent.changedTouches[0];
                    return this._handleMouseMove(e, this._getPositionInfo(touch));
                };

                MouseWidget.prototype._touchEnd = function (e) {
                    var touch;
                    if (e.originalEvent.touches.length > 1) {
                        return;
                    }
                    touch = e.originalEvent.changedTouches[0];
                    return this._handleMouseUp(this._getPositionInfo(touch));
                };

                return MouseWidget;

            })(SimpleWidget);

            module.exports = MouseWidget;

        }).call(this);

    }, {"./simple.widget": 11}], 6: [function (require, module, exports) {
        (function () {
            var Node, Position;

            Position = {
                getName: function (position) {
                    return Position.strings[position - 1];
                },
                nameToIndex: function (name) {
                    var i, _i, _ref;
                    for (i = _i = 1, _ref = Position.strings.length; 1 <= _ref ? _i <= _ref : _i >= _ref; i = 1 <= _ref ? ++_i : --_i) {
                        if (Position.strings[i - 1] === name) {
                            return i;
                        }
                    }
                    return 0;
                }
            };

            Position.BEFORE = 1;

            Position.AFTER = 2;

            Position.INSIDE = 3;

            Position.NONE = 4;

            Position.strings = ['before', 'after', 'inside', 'none'];

            Node = (function () {
                function Node(o, is_root, node_class) {
                    if (is_root == null) {
                        is_root = false;
                    }
                    if (node_class == null) {
                        node_class = Node;
                    }
                    this.setData(o);
                    this.children = [];
                    this.parent = null;
                    if (is_root) {
                        this.id_mapping = {};
                        this.tree = this;
                        this.node_class = node_class;
                    }
                }

                Node.prototype.setData = function (o) {
                    var key, value, _results;
                    if (typeof o !== 'object') {
                        return this.name = o;
                    } else {
                        _results = [];
                        for (key in o) {
                            value = o[key];
                            if (key === 'label') {
                                _results.push(this.name = value);
                            } else {
                                _results.push(this[key] = value);
                            }
                        }
                        return _results;
                    }
                };

                Node.prototype.initFromData = function (data) {
                    var addChildren, addNode;
                    addNode = (function (_this) {
                        return function (node_data) {
                            _this.setData(node_data);
                            if (node_data.children) {
                                return addChildren(node_data.children);
                            }
                        };
                    })(this);
                    addChildren = (function (_this) {
                        return function (children_data) {
                            var child, node, _i, _len;
                            for (_i = 0, _len = children_data.length; _i < _len; _i++) {
                                child = children_data[_i];
                                node = new _this.tree.node_class('');
                                node.initFromData(child);
                                _this.addChild(node);
                            }
                            return null;
                        };
                    })(this);
                    addNode(data);
                    return null;
                };


                /*
    Create tree from data.

    Structure of data is:
    [
        {
            label: 'node1',
            children: [
                { label: 'child1' },
                { label: 'child2' }
            ]
        },
        {
            label: 'node2'
        }
    ]
     */

                Node.prototype.loadFromData = function (data) {
                    var node, o, _i, _len;
                    this.removeChildren();
                    for (_i = 0, _len = data.length; _i < _len; _i++) {
                        o = data[_i];
                        node = new this.tree.node_class(o);
                        this.addChild(node);
                        if (typeof o === 'object' && o.children) {
                            node.loadFromData(o.children);
                        }
                    }
                    return null;
                };


                /*
    Add child.

    tree.addChild(
        new Node('child1')
    );
     */

                Node.prototype.addChild = function (node) {
                    this.children.push(node);
                    return node._setParent(this);
                };


                /*
    Add child at position. Index starts at 0.

    tree.addChildAtPosition(
        new Node('abc'),
        1
    );
     */

                Node.prototype.addChildAtPosition = function (node, index) {
                    this.children.splice(index, 0, node);
                    return node._setParent(this);
                };

                Node.prototype._setParent = function (parent) {
                    this.parent = parent;
                    this.tree = parent.tree;
                    return this.tree.addNodeToIndex(this);
                };


                /*
    Remove child. This also removes the children of the node.

    tree.removeChild(tree.children[0]);
     */

                Node.prototype.removeChild = function (node) {
                    node.removeChildren();
                    return this._removeChild(node);
                };

                Node.prototype._removeChild = function (node) {
                    this.children.splice(this.getChildIndex(node), 1);
                    return this.tree.removeNodeFromIndex(node);
                };


                /*
    Get child index.

    var index = getChildIndex(node);
     */

                Node.prototype.getChildIndex = function (node) {
                    return $.inArray(node, this.children);
                };


                /*
    Does the tree have children?

    if (tree.hasChildren()) {
        //
    }
     */

                Node.prototype.hasChildren = function () {
                    return this.children.length !== 0;
                };

                Node.prototype.isFolder = function () {
                    return this.hasChildren() || this.load_on_demand;
                };


                /*
    Iterate over all the nodes in the tree.

    Calls callback with (node, level).

    The callback must return true to continue the iteration on current node.

    tree.iterate(
        function(node, level) {
           console.log(node.name);

           // stop iteration after level 2
           return (level <= 2);
        }
    );
     */

                Node.prototype.iterate = function (callback) {
                    var _iterate;
                    _iterate = function (node, level) {
                        var child, result, _i, _len, _ref;
                        if (node.children) {
                            _ref = node.children;
                            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                                child = _ref[_i];
                                result = callback(child, level);
                                if (result && child.hasChildren()) {
                                    _iterate(child, level + 1);
                                }
                            }
                            return null;
                        }
                    };
                    _iterate(this, 0);
                    return null;
                };


                /*
    Move node relative to another node.

    Argument position: Position.BEFORE, Position.AFTER or Position.Inside

    // move node1 after node2
    tree.moveNode(node1, node2, Position.AFTER);
     */

                Node.prototype.moveNode = function (moved_node, target_node, position) {
                    if (moved_node.isParentOf(target_node)) {
                        return;
                    }
                    moved_node.parent._removeChild(moved_node);
                    if (position === Position.AFTER) {
                        return target_node.parent.addChildAtPosition(moved_node, target_node.parent.getChildIndex(target_node) + 1);
                    } else if (position === Position.BEFORE) {
                        return target_node.parent.addChildAtPosition(moved_node, target_node.parent.getChildIndex(target_node));
                    } else if (position === Position.INSIDE) {
                        return target_node.addChildAtPosition(moved_node, 0);
                    }
                };


                /*
    Get the tree as data.
     */

                Node.prototype.getData = function () {
                    var getDataFromNodes;
                    getDataFromNodes = (function (_this) {
                        return function (nodes) {
                            var data, k, node, tmp_node, v, _i, _len;
                            data = [];
                            for (_i = 0, _len = nodes.length; _i < _len; _i++) {
                                node = nodes[_i];
                                tmp_node = {};
                                for (k in node) {
                                    v = node[k];
                                    if ((k !== 'parent' && k !== 'children' && k !== 'element' && k !== 'tree') && Object.prototype.hasOwnProperty.call(node, k)) {
                                        tmp_node[k] = v;
                                    }
                                }
                                if (node.hasChildren()) {
                                    tmp_node.children = getDataFromNodes(node.children);
                                }
                                data.push(tmp_node);
                            }
                            return data;
                        };
                    })(this);
                    return getDataFromNodes(this.children);
                };

                Node.prototype.getNodeByName = function (name) {
                    var result;
                    result = null;
                    this.iterate(function (node) {
                        if (node.name === name) {
                            result = node;
                            return false;
                        } else {
                            return true;
                        }
                    });
                    return result;
                };

                Node.prototype.addAfter = function (node_info) {
                    var child_index, node;
                    if (!this.parent) {
                        return null;
                    } else {
                        node = new this.tree.node_class(node_info);
                        child_index = this.parent.getChildIndex(this);
                        this.parent.addChildAtPosition(node, child_index + 1);
                        return node;
                    }
                };

                Node.prototype.addBefore = function (node_info) {
                    var child_index, node;
                    if (!this.parent) {
                        return null;
                    } else {
                        node = new this.tree.node_class(node_info);
                        child_index = this.parent.getChildIndex(this);
                        this.parent.addChildAtPosition(node, child_index);
                        return node;
                    }
                };

                Node.prototype.addParent = function (node_info) {
                    var child, new_parent, original_parent, _i, _len, _ref;
                    if (!this.parent) {
                        return null;
                    } else {
                        new_parent = new this.tree.node_class(node_info);
                        new_parent._setParent(this.tree);
                        original_parent = this.parent;
                        _ref = original_parent.children;
                        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                            child = _ref[_i];
                            new_parent.addChild(child);
                        }
                        original_parent.children = [];
                        original_parent.addChild(new_parent);
                        return new_parent;
                    }
                };

                Node.prototype.remove = function () {
                    if (this.parent) {
                        this.parent.removeChild(this);
                        return this.parent = null;
                    }
                };

                Node.prototype.append = function (node_info) {
                    var node;
                    node = new this.tree.node_class(node_info);
                    this.addChild(node);
                    return node;
                };

                Node.prototype.prepend = function (node_info) {
                    var node;
                    node = new this.tree.node_class(node_info);
                    this.addChildAtPosition(node, 0);
                    return node;
                };

                Node.prototype.isParentOf = function (node) {
                    var parent;
                    parent = node.parent;
                    while (parent) {
                        if (parent === this) {
                            return true;
                        }
                        parent = parent.parent;
                    }
                    return false;
                };

                Node.prototype.getLevel = function () {
                    var level, node;
                    level = 0;
                    node = this;
                    while (node.parent) {
                        level += 1;
                        node = node.parent;
                    }
                    return level;
                };

                Node.prototype.getNodeById = function (node_id) {
                    return this.id_mapping[node_id];
                };

                Node.prototype.addNodeToIndex = function (node) {
                    if (node.id != null) {
                        return this.id_mapping[node.id] = node;
                    }
                };

                Node.prototype.removeNodeFromIndex = function (node) {
                    if (node.id != null) {
                        return delete this.id_mapping[node.id];
                    }
                };

                Node.prototype.removeChildren = function () {
                    this.iterate((function (_this) {
                        return function (child) {
                            _this.tree.removeNodeFromIndex(child);
                            return true;
                        };
                    })(this));
                    return this.children = [];
                };

                Node.prototype.getPreviousSibling = function () {
                    var previous_index;
                    if (!this.parent) {
                        return null;
                    } else {
                        previous_index = this.parent.getChildIndex(this) - 1;
                        if (previous_index >= 0) {
                            return this.parent.children[previous_index];
                        } else {
                            return null;
                        }
                    }
                };

                Node.prototype.getNextSibling = function () {
                    var next_index;
                    if (!this.parent) {
                        return null;
                    } else {
                        next_index = this.parent.getChildIndex(this) + 1;
                        if (next_index < this.parent.children.length) {
                            return this.parent.children[next_index];
                        } else {
                            return null;
                        }
                    }
                };

                Node.prototype.getNodesByProperty = function (key, value) {
                    return this.filter(function (node) {
                        return node[key] === value;
                    });
                };

                Node.prototype.filter = function (f) {
                    var result;
                    result = [];
                    this.iterate(function (node) {
                        if (f(node)) {
                            result.push(node);
                        }
                        return true;
                    });
                    return result;
                };

                Node.prototype.getNextNode = function (include_children) {
                    var next_sibling;
                    if (include_children == null) {
                        include_children = true;
                    }
                    if (include_children && this.hasChildren() && this.is_open) {
                        return this.children[0];
                    } else {
                        if (!this.parent) {
                            return null;
                        } else {
                            next_sibling = this.getNextSibling();
                            if (next_sibling) {
                                return next_sibling;
                            } else {
                                return this.parent.getNextNode(false);
                            }
                        }
                    }
                };

                Node.prototype.getPreviousNode = function () {
                    var previous_sibling;
                    if (!this.parent) {
                        return null;
                    } else {
                        previous_sibling = this.getPreviousSibling();
                        if (previous_sibling) {
                            if (!previous_sibling.hasChildren() || !previous_sibling.is_open) {
                                return previous_sibling;
                            } else {
                                return previous_sibling.getLastChild();
                            }
                        } else {
                            if (this.parent.parent) {
                                return this.parent;
                            } else {
                                return null;
                            }
                        }
                    }
                };

                Node.prototype.getLastChild = function () {
                    var last_child;
                    if (!this.hasChildren()) {
                        return null;
                    } else {
                        last_child = this.children[this.children.length - 1];
                        if (!last_child.hasChildren() || !last_child.is_open) {
                            return last_child;
                        } else {
                            return last_child.getLastChild();
                        }
                    }
                };

                return Node;

            })();

            module.exports = {
                Node: Node,
                Position: Position
            };

        }).call(this);

    }, {}], 7: [function (require, module, exports) {
        (function () {
            var BorderDropHint, FolderElement, GhostDropHint, NodeElement, Position, node,
                __hasProp = {}.hasOwnProperty,
                __extends = function (child, parent) {
                    for (var key in parent) {
                        if (__hasProp.call(parent, key)) child[key] = parent[key];
                    }

                    function ctor() {
                        this.constructor = child;
                    }

                    ctor.prototype = parent.prototype;
                    child.prototype = new ctor();
                    child.__super__ = parent.prototype;
                    return child;
                };

            node = require('./node');

            Position = node.Position;

            NodeElement = (function () {
                function NodeElement(node, tree_widget) {
                    this.init(node, tree_widget);
                }

                NodeElement.prototype.init = function (node, tree_widget) {
                    this.node = node;
                    this.tree_widget = tree_widget;
                    if (!node.element) {
                        node.element = this.tree_widget.element;
                    }
                    return this.$element = $(node.element);
                };

                NodeElement.prototype.getUl = function () {
                    return this.$element.children('ul:first');
                };

                NodeElement.prototype.getSpan = function () {
                    return this.$element.children('.jqtree-element').find('span.jqtree-title');
                };

                NodeElement.prototype.getLi = function () {
                    return this.$element;
                };

                NodeElement.prototype.addDropHint = function (position) {
                    if (position === Position.INSIDE) {
                        return new BorderDropHint(this.$element);
                    } else {
                        return new GhostDropHint(this.node, this.$element, position);
                    }
                };

                NodeElement.prototype.select = function () {
                    return this.getLi().addClass('jqtree-selected');
                };

                NodeElement.prototype.deselect = function () {
                    return this.getLi().removeClass('jqtree-selected');
                };

                return NodeElement;

            })();

            FolderElement = (function (_super) {
                __extends(FolderElement, _super);

                function FolderElement() {
                    return FolderElement.__super__.constructor.apply(this, arguments);
                }

                FolderElement.prototype.open = function (on_finished, slide) {
                    var $button, doOpen;
                    if (slide == null) {
                        slide = true;
                    }
                    if (!this.node.is_open) {
                        this.node.is_open = true;
                        $button = this.getButton();
                        $button.removeClass('jqtree-closed');
                        $button.html('');
                        $button.append(this.tree_widget.renderer.opened_icon_element.cloneNode(false));
                        doOpen = (function (_this) {
                            return function () {
                                _this.getLi().removeClass('jqtree-closed');
                                if (on_finished) {
                                    on_finished();
                                }
                                return _this.tree_widget._triggerEvent('tree.open', {
                                    node: _this.node
                                });
                            };
                        })(this);
                        if (slide) {
                            return this.getUl().slideDown('fast', doOpen);
                        } else {
                            this.getUl().show();
                            return doOpen();
                        }
                    }
                };

                FolderElement.prototype.close = function (slide) {
                    var $button, doClose;
                    if (slide == null) {
                        slide = true;
                    }
                    if (this.node.is_open) {
                        this.node.is_open = false;
                        $button = this.getButton();
                        $button.addClass('jqtree-closed');
                        $button.html('');
                        $button.append(this.tree_widget.renderer.closed_icon_element.cloneNode(false));
                        doClose = (function (_this) {
                            return function () {
                                _this.getLi().addClass('jqtree-closed');
                                return _this.tree_widget._triggerEvent('tree.close', {
                                    node: _this.node
                                });
                            };
                        })(this);
                        if (slide) {
                            return this.getUl().slideUp('fast', doClose);
                        } else {
                            this.getUl().hide();
                            return doClose();
                        }
                    }
                };

                FolderElement.prototype.getButton = function () {
                    return this.$element.children('.jqtree-element').find('a.jqtree-toggler');
                };

                FolderElement.prototype.addDropHint = function (position) {
                    if (!this.node.is_open && position === Position.INSIDE) {
                        return new BorderDropHint(this.$element);
                    } else {
                        return new GhostDropHint(this.node, this.$element, position);
                    }
                };

                return FolderElement;

            })(NodeElement);

            BorderDropHint = (function () {
                function BorderDropHint($element) {
                    var $div, width;
                    $div = $element.children('.jqtree-element');
                    width = $element.width() - 4;
                    this.$hint = $('<span class="jqtree-border"></span>');
                    $div.append(this.$hint);
                    this.$hint.css({
                        width: width,
                        height: $div.outerHeight() - 4
                    });
                }

                BorderDropHint.prototype.remove = function () {
                    return this.$hint.remove();
                };

                return BorderDropHint;

            })();

            GhostDropHint = (function () {
                function GhostDropHint(node, $element, position) {
                    this.$element = $element;
                    this.node = node;
                    this.$ghost = $('<li class="jqtree_common jqtree-ghost"><span class="jqtree_common jqtree-circle"></span><span class="jqtree_common jqtree-line"></span></li>');
                    if (position === Position.AFTER) {
                        this.moveAfter();
                    } else if (position === Position.BEFORE) {
                        this.moveBefore();
                    } else if (position === Position.INSIDE) {
                        if (node.isFolder() && node.is_open) {
                            this.moveInsideOpenFolder();
                        } else {
                            this.moveInside();
                        }
                    }
                }

                GhostDropHint.prototype.remove = function () {
                    return this.$ghost.remove();
                };

                GhostDropHint.prototype.moveAfter = function () {
                    return this.$element.after(this.$ghost);
                };

                GhostDropHint.prototype.moveBefore = function () {
                    return this.$element.before(this.$ghost);
                };

                GhostDropHint.prototype.moveInsideOpenFolder = function () {
                    return $(this.node.children[0].element).before(this.$ghost);
                };

                GhostDropHint.prototype.moveInside = function () {
                    this.$element.after(this.$ghost);
                    return this.$ghost.addClass('jqtree-inside');
                };

                return GhostDropHint;

            })();

            module.exports = {
                FolderElement: FolderElement,
                NodeElement: NodeElement
            };

        }).call(this);

    }, {"./node": 6}], 8: [function (require, module, exports) {
        (function () {
            var SaveStateHandler, indexOf, isInt, util;

            util = require('./util');

            indexOf = util.indexOf;

            isInt = util.isInt;

            SaveStateHandler = (function () {
                function SaveStateHandler(tree_widget) {
                    this.tree_widget = tree_widget;
                }

                SaveStateHandler.prototype.saveState = function () {
                    var state;
                    state = JSON.stringify(this.getState());
                    if (this.tree_widget.options.onSetStateFromStorage) {
                        return this.tree_widget.options.onSetStateFromStorage(state);
                    } else if (this.supportsLocalStorage()) {
                        return localStorage.setItem(this.getCookieName(), state);
                    } else if ($.cookie) {
                        $.cookie.raw = true;
                        return $.cookie(this.getCookieName(), state, {
                            path: '/'
                        });
                    }
                };

                SaveStateHandler.prototype.getStateFromStorage = function () {
                    var json_data;
                    json_data = this._loadFromStorage();
                    if (json_data) {
                        return this._parseState(json_data);
                    } else {
                        return null;
                    }
                };

                SaveStateHandler.prototype._parseState = function (json_data) {
                    var state;
                    state = $.parseJSON(json_data);
                    if (state && state.selected_node && isInt(state.selected_node)) {
                        state.selected_node = [state.selected_node];
                    }
                    return state;
                };

                SaveStateHandler.prototype._loadFromStorage = function () {
                    if (this.tree_widget.options.onGetStateFromStorage) {
                        return this.tree_widget.options.onGetStateFromStorage();
                    } else if (this.supportsLocalStorage()) {
                        return localStorage.getItem(this.getCookieName());
                    } else if ($.cookie) {
                        $.cookie.raw = true;
                        return $.cookie(this.getCookieName());
                    } else {
                        return null;
                    }
                };

                SaveStateHandler.prototype.getState = function () {
                    var getOpenNodeIds, getSelectedNodeIds;
                    getOpenNodeIds = (function (_this) {
                        return function () {
                            var open_nodes;
                            open_nodes = [];
                            _this.tree_widget.tree.iterate(function (node) {
                                if (node.is_open && node.id && node.hasChildren()) {
                                    open_nodes.push(node.id);
                                }
                                return true;
                            });
                            return open_nodes;
                        };
                    })(this);
                    getSelectedNodeIds = (function (_this) {
                        return function () {
                            var n;
                            return (function () {
                                var _i, _len, _ref, _results;
                                _ref = this.tree_widget.getSelectedNodes();
                                _results = [];
                                for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                                    n = _ref[_i];
                                    _results.push(n.id);
                                }
                                return _results;
                            }).call(_this);
                        };
                    })(this);
                    return {
                        open_nodes: getOpenNodeIds(),
                        selected_node: getSelectedNodeIds()
                    };
                };

                SaveStateHandler.prototype.setInitialState = function (state) {
                    var must_load_on_demand;
                    if (!state) {
                        return false;
                    } else {
                        must_load_on_demand = this._openInitialNodes(state.open_nodes);
                        this._selectInitialNodes(state.selected_node);
                        return must_load_on_demand;
                    }
                };

                SaveStateHandler.prototype._openInitialNodes = function (node_ids) {
                    var must_load_on_demand, node, node_id, _i, _len;
                    must_load_on_demand = false;
                    for (_i = 0, _len = node_ids.length; _i < _len; _i++) {
                        node_id = node_ids[_i];
                        node = this.tree_widget.getNodeById(node_id);
                        if (node) {
                            if (!node.load_on_demand) {
                                node.is_open = true;
                            } else {
                                must_load_on_demand = true;
                            }
                        }
                    }
                    return must_load_on_demand;
                };

                SaveStateHandler.prototype._selectInitialNodes = function (node_ids) {
                    var node, node_id, select_count, _i, _len;
                    select_count = 0;
                    for (_i = 0, _len = node_ids.length; _i < _len; _i++) {
                        node_id = node_ids[_i];
                        node = this.tree_widget.getNodeById(node_id);
                        if (node) {
                            select_count += 1;
                            this.tree_widget.select_node_handler.addToSelection(node);
                        }
                    }
                    return select_count !== 0;
                };

                SaveStateHandler.prototype.setInitialStateOnDemand = function (state) {
                    if (state) {
                        return this._setInitialStateOnDemand(state.open_nodes, state.selected_node);
                    }
                };

                SaveStateHandler.prototype._setInitialStateOnDemand = function (node_ids, selected_nodes) {
                    var loadAndOpenNode, openNodes;
                    openNodes = (function (_this) {
                        return function () {
                            var new_nodes_ids, node, node_id, _i, _len;
                            new_nodes_ids = [];
                            for (_i = 0, _len = node_ids.length; _i < _len; _i++) {
                                node_id = node_ids[_i];
                                node = _this.tree_widget.getNodeById(node_id);
                                if (!node) {
                                    new_nodes_ids.push(node_id);
                                } else {
                                    if (!node.is_loading) {
                                        if (node.load_on_demand) {
                                            loadAndOpenNode(node);
                                        } else {
                                            _this.tree_widget._openNode(node, false);
                                        }
                                    }
                                }
                            }
                            node_ids = new_nodes_ids;
                            if (_this._selectInitialNodes(selected_nodes)) {
                                return _this.tree_widget._refreshElements();
                            }
                        };
                    })(this);
                    loadAndOpenNode = (function (_this) {
                        return function (node) {
                            return _this.tree_widget._openNode(node, false, openNodes);
                        };
                    })(this);
                    return openNodes();
                };

                SaveStateHandler.prototype.getCookieName = function () {
                    if (typeof this.tree_widget.options.saveState === 'string') {
                        return this.tree_widget.options.saveState;
                    } else {
                        return 'tree';
                    }
                };

                SaveStateHandler.prototype.supportsLocalStorage = function () {
                    var testSupport;
                    testSupport = function () {
                        var error, key;
                        if (typeof localStorage === "undefined" || localStorage === null) {
                            return false;
                        } else {
                            try {
                                key = '_storage_test';
                                sessionStorage.setItem(key, true);
                                sessionStorage.removeItem(key);
                            } catch (_error) {
                                error = _error;
                                return false;
                            }
                            return true;
                        }
                    };
                    if (this._supportsLocalStorage == null) {
                        this._supportsLocalStorage = testSupport();
                    }
                    return this._supportsLocalStorage;
                };

                SaveStateHandler.prototype.getNodeIdToBeSelected = function () {
                    var state;
                    state = this.getStateFromStorage();
                    if (state && state.selected_node) {
                        return state.selected_node[0];
                    } else {
                        return null;
                    }
                };

                return SaveStateHandler;

            })();

            module.exports = SaveStateHandler;

        }).call(this);

    }, {"./util": 12}], 9: [function (require, module, exports) {
        (function () {
            var ScrollHandler;

            ScrollHandler = (function () {
                function ScrollHandler(tree_widget) {
                    this.tree_widget = tree_widget;
                    this.previous_top = -1;
                    this._initScrollParent();
                }

                ScrollHandler.prototype._initScrollParent = function () {
                    var $scroll_parent, getParentWithOverflow, setDocumentAsScrollParent;
                    getParentWithOverflow = (function (_this) {
                        return function () {
                            var css_values, el, hasOverFlow, _i, _len, _ref;
                            css_values = ['overflow', 'overflow-y'];
                            hasOverFlow = function (el) {
                                var css_value, _i, _len, _ref;
                                for (_i = 0, _len = css_values.length; _i < _len; _i++) {
                                    css_value = css_values[_i];
                                    if ((_ref = $.css(el, css_value)) === 'auto' || _ref === 'scroll') {
                                        return true;
                                    }
                                }
                                return false;
                            };
                            if (hasOverFlow(_this.tree_widget.$el[0])) {
                                return _this.tree_widget.$el;
                            }
                            _ref = _this.tree_widget.$el.parents();
                            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                                el = _ref[_i];
                                if (hasOverFlow(el)) {
                                    return $(el);
                                }
                            }
                            return null;
                        };
                    })(this);
                    setDocumentAsScrollParent = (function (_this) {
                        return function () {
                            _this.scroll_parent_top = 0;
                            return _this.$scroll_parent = null;
                        };
                    })(this);
                    if (this.tree_widget.$el.css('position') === 'fixed') {
                        setDocumentAsScrollParent();
                    }
                    $scroll_parent = getParentWithOverflow();
                    if ($scroll_parent && $scroll_parent.length && $scroll_parent[0].tagName !== 'HTML') {
                        this.$scroll_parent = $scroll_parent;
                        return this.scroll_parent_top = this.$scroll_parent.offset().top;
                    } else {
                        return setDocumentAsScrollParent();
                    }
                };

                ScrollHandler.prototype.checkScrolling = function () {
                    var hovered_area;
                    hovered_area = this.tree_widget.dnd_handler.hovered_area;
                    if (hovered_area && hovered_area.top !== this.previous_top) {
                        this.previous_top = hovered_area.top;
                        if (this.$scroll_parent) {
                            return this._handleScrollingWithScrollParent(hovered_area);
                        } else {
                            return this._handleScrollingWithDocument(hovered_area);
                        }
                    }
                };

                ScrollHandler.prototype._handleScrollingWithScrollParent = function (area) {
                    var distance_bottom;
                    distance_bottom = this.scroll_parent_top + this.$scroll_parent[0].offsetHeight - area.bottom;
                    if (distance_bottom < 20) {
                        this.$scroll_parent[0].scrollTop += 20;
                        this.tree_widget.refreshHitAreas();
                        return this.previous_top = -1;
                    } else if ((area.top - this.scroll_parent_top) < 20) {
                        this.$scroll_parent[0].scrollTop -= 20;
                        this.tree_widget.refreshHitAreas();
                        return this.previous_top = -1;
                    }
                };

                ScrollHandler.prototype._handleScrollingWithDocument = function (area) {
                    var distance_top;
                    distance_top = area.top - $(document).scrollTop();
                    if (distance_top < 20) {
                        return $(document).scrollTop($(document).scrollTop() - 20);
                    } else if ($(window).height() - (area.bottom - $(document).scrollTop()) < 20) {
                        return $(document).scrollTop($(document).scrollTop() + 20);
                    }
                };

                ScrollHandler.prototype.scrollTo = function (top) {
                    var tree_top;
                    if (this.$scroll_parent) {
                        return this.$scroll_parent[0].scrollTop = top;
                    } else {
                        tree_top = this.tree_widget.$el.offset().top;
                        return $(document).scrollTop(top + tree_top);
                    }
                };

                ScrollHandler.prototype.isScrolledIntoView = function (element) {
                    var $element, element_bottom, element_top, view_bottom, view_top;
                    $element = $(element);
                    if (this.$scroll_parent) {
                        view_top = 0;
                        view_bottom = this.$scroll_parent.height();
                        element_top = $element.offset().top - this.scroll_parent_top;
                        element_bottom = element_top + $element.height();
                    } else {
                        view_top = $(window).scrollTop();
                        view_bottom = view_top + $(window).height();
                        element_top = $element.offset().top;
                        element_bottom = element_top + $element.height();
                    }
                    return (element_bottom <= view_bottom) && (element_top >= view_top);
                };

                return ScrollHandler;

            })();

            module.exports = ScrollHandler;

        }).call(this);

    }, {}], 10: [function (require, module, exports) {
        (function () {
            var SelectNodeHandler;

            SelectNodeHandler = (function () {
                function SelectNodeHandler(tree_widget) {
                    this.tree_widget = tree_widget;
                    this.clear();
                }

                SelectNodeHandler.prototype.getSelectedNode = function () {
                    var selected_nodes;
                    selected_nodes = this.getSelectedNodes();
                    if (selected_nodes.length) {
                        return selected_nodes[0];
                    } else {
                        return false;
                    }
                };

                SelectNodeHandler.prototype.getSelectedNodes = function () {
                    var id, node, selected_nodes;
                    if (this.selected_single_node) {
                        return [this.selected_single_node];
                    } else {
                        selected_nodes = [];
                        for (id in this.selected_nodes) {
                            node = this.tree_widget.getNodeById(id);
                            if (node) {
                                selected_nodes.push(node);
                            }
                        }
                        return selected_nodes;
                    }
                };

                SelectNodeHandler.prototype.getSelectedNodesUnder = function (parent) {
                    var id, node, selected_nodes;
                    if (this.selected_single_node) {
                        if (parent.isParentOf(this.selected_single_node)) {
                            return [this.selected_single_node];
                        } else {
                            return [];
                        }
                    } else {
                        selected_nodes = [];
                        for (id in this.selected_nodes) {
                            node = this.tree_widget.getNodeById(id);
                            if (node && parent.isParentOf(node)) {
                                selected_nodes.push(node);
                            }
                        }
                        return selected_nodes;
                    }
                };

                SelectNodeHandler.prototype.isNodeSelected = function (node) {
                    if (node.id) {
                        return this.selected_nodes[node.id];
                    } else if (this.selected_single_node) {
                        return this.selected_single_node.element === node.element;
                    } else {
                        return false;
                    }
                };

                SelectNodeHandler.prototype.clear = function () {
                    this.selected_nodes = {};
                    return this.selected_single_node = null;
                };

                SelectNodeHandler.prototype.removeFromSelection = function (node, include_children) {
                    if (include_children == null) {
                        include_children = false;
                    }
                    if (!node.id) {
                        if (this.selected_single_node && node.element === this.selected_single_node.element) {
                            return this.selected_single_node = null;
                        }
                    } else {
                        delete this.selected_nodes[node.id];
                        if (include_children) {
                            return node.iterate((function (_this) {
                                return function (n) {
                                    delete _this.selected_nodes[node.id];
                                    return true;
                                };
                            })(this));
                        }
                    }
                };

                SelectNodeHandler.prototype.addToSelection = function (node) {
                    if (node.id) {
                        return this.selected_nodes[node.id] = true;
                    } else {
                        return this.selected_single_node = node;
                    }
                };

                return SelectNodeHandler;

            })();

            module.exports = SelectNodeHandler;

        }).call(this);

    }, {}], 11: [function (require, module, exports) {

        /*
Copyright 2013 Marco Braak

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

        (function () {
            var SimpleWidget,
                __slice = [].slice;

            SimpleWidget = (function () {
                SimpleWidget.prototype.defaults = {};

                function SimpleWidget(el, options) {
                    this.$el = $(el);
                    this.options = $.extend({}, this.defaults, options);
                }

                SimpleWidget.prototype.destroy = function () {
                    return this._deinit();
                };

                SimpleWidget.prototype._init = function () {
                    return null;
                };

                SimpleWidget.prototype._deinit = function () {
                    return null;
                };

                SimpleWidget.register = function (widget_class, widget_name) {
                    var callFunction, createWidget, destroyWidget, getDataKey, getWidgetData;
                    getDataKey = function () {
                        return "simple_widget_" + widget_name;
                    };
                    getWidgetData = function (el, data_key) {
                        var widget;
                        widget = $.data(el, data_key);
                        if (widget && (widget instanceof SimpleWidget)) {
                            return widget;
                        } else {
                            return null;
                        }
                    };
                    createWidget = function ($el, options) {
                        var data_key, el, existing_widget, widget, _i, _len;
                        data_key = getDataKey();
                        for (_i = 0, _len = $el.length; _i < _len; _i++) {
                            el = $el[_i];
                            existing_widget = getWidgetData(el, data_key);
                            if (!existing_widget) {
                                widget = new widget_class(el, options);
                                if (!$.data(el, data_key)) {
                                    $.data(el, data_key, widget);
                                }
                                widget._init();
                            }
                        }
                        return $el;
                    };
                    destroyWidget = function ($el) {
                        var data_key, el, widget, _i, _len, _results;
                        data_key = getDataKey();
                        _results = [];
                        for (_i = 0, _len = $el.length; _i < _len; _i++) {
                            el = $el[_i];
                            widget = getWidgetData(el, data_key);
                            if (widget) {
                                widget.destroy();
                            }
                            _results.push($.removeData(el, data_key));
                        }
                        return _results;
                    };
                    callFunction = function ($el, function_name, args) {
                        var el, result, widget, widget_function, _i, _len;
                        result = null;
                        for (_i = 0, _len = $el.length; _i < _len; _i++) {
                            el = $el[_i];
                            widget = $.data(el, getDataKey());
                            if (widget && (widget instanceof SimpleWidget)) {
                                widget_function = widget[function_name];
                                if (widget_function && (typeof widget_function === 'function')) {
                                    result = widget_function.apply(widget, args);
                                }
                            }
                        }
                        return result;
                    };
                    return $.fn[widget_name] = function () {
                        var $el, args, argument1, function_name, options;
                        argument1 = arguments[0], args = 2 <= arguments.length ? __slice.call(arguments, 1) : [];
                        $el = this;
                        if (argument1 === void 0 || typeof argument1 === 'object') {
                            options = argument1;
                            return createWidget($el, options);
                        } else if (typeof argument1 === 'string' && argument1[0] !== '_') {
                            function_name = argument1;
                            if (function_name === 'destroy') {
                                return destroyWidget($el);
                            } else {
                                return callFunction($el, function_name, args);
                            }
                        }
                    };
                };

                return SimpleWidget;

            })();

            module.exports = SimpleWidget;

        }).call(this);

    }, {}], 12: [function (require, module, exports) {
        (function () {
            var get_json_stringify_function, html_escape, indexOf, isInt, _indexOf;

            _indexOf = function (array, item) {
                var i, value, _i, _len;
                for (i = _i = 0, _len = array.length; _i < _len; i = ++_i) {
                    value = array[i];
                    if (value === item) {
                        return i;
                    }
                }
                return -1;
            };

            indexOf = function (array, item) {
                if (array.indexOf) {
                    return array.indexOf(item);
                } else {
                    return _indexOf(array, item);
                }
            };

            isInt = function (n) {
                return typeof n === 'number' && n % 1 === 0;
            };

            get_json_stringify_function = function () {
                var json_escapable, json_meta, json_quote, json_str, stringify;
                json_escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g;
                json_meta = {
                    '\b': '\\b',
                    '\t': '\\t',
                    '\n': '\\n',
                    '\f': '\\f',
                    '\r': '\\r',
                    '"': '\\"',
                    '\\': '\\\\'
                };
                json_quote = function (string) {
                    json_escapable.lastIndex = 0;
                    if (json_escapable.test(string)) {
                        return '"' + string.replace(json_escapable, function (a) {
                            var c;
                            c = json_meta[a];
                            return (typeof c === 'string' ? c : '\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4));
                        }) + '"';
                    } else {
                        return '"' + string + '"';
                    }
                };
                json_str = function (key, holder) {
                    var i, k, partial, v, value, _i, _len;
                    value = holder[key];
                    switch (typeof value) {
                        case 'string':
                            return json_quote(value);
                        case 'number':
                            if (isFinite(value)) {
                                return String(value);
                            } else {
                                return 'null';
                            }
                        case 'boolean':
                        case 'null':
                            return String(value);
                        case 'object':
                            if (!value) {
                                return 'null';
                            }
                            partial = [];
                            if (Object.prototype.toString.apply(value) === '[object Array]') {
                                for (i = _i = 0, _len = value.length; _i < _len; i = ++_i) {
                                    v = value[i];
                                    partial[i] = json_str(i, value) || 'null';
                                }
                                return (partial.length === 0 ? '[]' : '[' + partial.join(',') + ']');
                            }
                            for (k in value) {
                                if (Object.prototype.hasOwnProperty.call(value, k)) {
                                    v = json_str(k, value);
                                    if (v) {
                                        partial.push(json_quote(k) + ':' + v);
                                    }
                                }
                            }
                            return (partial.length === 0 ? '{}' : '{' + partial.join(',') + '}');
                    }
                };
                stringify = function (value) {
                    return json_str('', {
                        '': value
                    });
                };
                return stringify;
            };

            if (!((this.JSON != null) && (this.JSON.stringify != null) && typeof this.JSON.stringify === 'function')) {
                if (this.JSON == null) {
                    this.JSON = {};
                }
                this.JSON.stringify = get_json_stringify_function();
            }

            html_escape = function (string) {
                return ('' + string).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#x27;').replace(/\//g, '&#x2F;');
            };

            module.exports = {
                _indexOf: _indexOf,
                get_json_stringify_function: get_json_stringify_function,
                html_escape: html_escape,
                indexOf: indexOf,
                isInt: isInt
            };

        }).call(this);

    }, {}], 13: [function (require, module, exports) {
        (function () {
            module.exports = '0.22.0';

        }).call(this);

    }, {}]
}, {}, [3]);