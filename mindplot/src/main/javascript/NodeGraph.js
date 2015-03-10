/*
 *    Copyright [2012] [wisemapping]
 *
 *   Licensed under WiseMapping Public License, Version 1.0 (the "License").
 *   It is basically the Apache License, Version 2.0 (the "License") plus the
 *   "powered by wisemapping" text requirement on every single page;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the license at
 *
 *       http://www.wisemapping.org/license
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

mindplot.NodeGraph = new Class({
    initialize:function(nodeModel, options) {
        $assert(nodeModel, "model can not be null");

        this._options = options;
        this._mouseEvents = true;
        this.setModel(nodeModel);
        this._onFocus = false;
        this._size = {width:50,height:20};
    },

    isReadOnly : function(){
        return this._options.readOnly;
    },

    getType : function() {
        var model = this.getModel();
        return model.getType();
    },

    setId : function(id) {
        $assert(typeof  topic.getId() == "number", "id is not a number:" + id);
        this.getModel().setId(id);
    },

    _set2DElement : function(elem2d) {
        this._elem2d = elem2d;
    },

    get2DElement : function() {
        $assert(this._elem2d, 'NodeGraph has not been initialized properly');
        return this._elem2d;
    },

    setPosition : function(point, fireEvent) {
        throw "Unsupported operation";
    },

    addEvent : function(type, listener) {
        var elem = this.get2DElement();
        elem.addEvent(type, listener);
    },

    removeEvent : function(type, listener) {
        var elem = this.get2DElement();
        elem.removeEvent(type, listener);
    },

    fireEvent: function(type, event) {
        var elem = this.get2DElement();
        elem.trigger(type, event);
    },

    setMouseEventsEnabled : function(isEnabled) {
        this._mouseEvents = isEnabled;
    },

    isMouseEventsEnabled : function() {
        return this._mouseEvents;
    },

    getSize : function() {
        return this._size;
    },

    setSize : function(size) {
        this._size.width = parseInt(size.width);
        this._size.height = parseInt(size.height);
    },

    getModel:function() {
        $assert(this._model, 'Model has not been initialized yet');
        return  this._model;
    },

    setModel : function(model) {
        $assert(model, 'Model can not be null');
        this._model = model;
    },

    getId : function() {
        return this._model.getId();
    },

    setOnFocus : function(focus) {
        if (this._onFocus != focus) {

            this._onFocus = focus;
            var outerShape = this.getOuterShape();
            var imageShape = this.getShapeType() == mindplot.model.TopicShape.IMAGE;
            if (focus) {
                if (imageShape) {
                    outerShape.previousFill = outerShape.getFill();                    
                }
                outerShape.setFill(mindplot.Topic.OUTER_SHAPE_ATTRIBUTES_FOCUS.fillColor, 1);
                outerShape.setStroke(null,null,mindplot.Topic.OUTER_SHAPE_ATTRIBUTES_FOCUS.fillColor, 1);

            } else {
                if (imageShape) {
                    outerShape.setFill(outerShape.previousFill.color, outerShape.previousFill.opacity);
                    outerShape.setStroke(null, null, outerShape.previousFill.color, outerShape.previousFill.opacity);
                } else {
                    outerShape.setFill(mindplot.Topic.OUTER_SHAPE_ATTRIBUTES.fillColor);
                    outerShape.setOpacity(0);
                }
                
            }
            this.setCursor('move');

            // In any case, always try to hide the editor ...
            this.closeEditors();

            // Fire event ...
            this.fireEvent(focus ? 'ontfocus' : 'ontblur',this);

        }
    },

    isOnFocus : function() {
        return this._onFocus;
    },

    dispose : function(workspace) {
        this.setOnFocus(false);
        workspace.removeChild(this);
    },

    createDragNode : function(layoutManager) {
        var dragShape = this._buildDragShape();
        return  new mindplot.DragTopic(dragShape, this, layoutManager);
    },

    _buildDragShape : function() {
        $assert(false, '_buildDragShape must be implemented by all nodes.');
    },

    getPosition : function() {
        var model = this.getModel();
        return model.getPosition();
    }
});

mindplot.NodeGraph.create = function(nodeModel, options) {
    $assert(nodeModel, 'Model can not be null');

    var type = nodeModel.getType();
    $assert(type, 'Node model type can not be null');

    var result;
    if (type == mindplot.model.INodeModel.CENTRAL_TOPIC_TYPE) {
        result = new mindplot.CentralTopic(nodeModel, options);
    } else
    if (type == mindplot.model.INodeModel.MAIN_TOPIC_TYPE) {
        result = new mindplot.MainTopic(nodeModel, options);
    } else {
        $assert(false, "unsupported node type:" + type);
    }

    return result;
};