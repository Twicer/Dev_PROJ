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

mindplot.widget.LinkIconTooltip = new Class({
    Extends:mindplot.widget.FloatingTip,

    initialize:function (linkIcon) {
        $assert(linkIcon, "linkIcon can not be null");
        this.parent($(linkIcon.getImage()._peer._native), {
            // Content can also be a function of the target element!
            content:this._buildContent(linkIcon),
            html:true,
            placement:'bottom',
            container: 'body',
            title: $msg('LINK')
        });
    },

    _buildContent:function (linkIcon) {
        var result = $('<div></div>').css({
            padding:'5px',
            width:'100%'
        });

        var text = $('<div></div>').text("URL: " + linkIcon.getModel().getUrl())
        .css({
            'white-space':'pre-wrap',
            'word-wrap':'break-word'
        });
        result.append(text);

        var imgContainer = $('<div></div>')
        .css({
            width:'100%',
            'textAlign':'right',
            'padding-bottom':'5px',
            'padding-top':'5px'
        });

        var img = $('<img>')
            .prop('src', 'http://immediatenet.com/t/m?Size=1024x768&URL=' + linkIcon.getModel().getUrl())
            .prop('img', linkIcon.getModel().getUrl())
            .prop('alt', linkIcon.getModel().getUrl());

        img.css('padding', '5px');

        var link = $('<a></a>').attr({
            href:linkIcon.getModel().getUrl(),
            alt:'Open in new window ...',
            target:'_blank'
        });

        link.append(img);
        imgContainer.append(link);
        result.append(imgContainer);
        return result;
    }
});