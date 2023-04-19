function setZIndex(thisWindow) {
    var windows = $('.window');
    var maxZ = Math.max.apply(1, windows.map(function(){
        return $(this).css('z-index');
    }));
    if(thisWindow.css('z-index') < maxZ) {
        var thisWindowFlag = false;
        windows.sort(function(a, b){
            return ($(a).css('z-index') < $(b).css('z-index') ? 1 : -1);
        }).each(function(){
            if($(this).css('z-index') === thisWindow.css('z-index')) {
                $(this).css('z-index', maxZ);
                thisWindowFlag = true;
            } else if(!thisWindowFlag) {
                $(this).css('z-index', $(this).css('z-index') - 1);
            }
        });
    }
}

function searchParentWindow(button) {
    return button.parents('.window')
}


$(function(){
    $('.window').draggable({
        handle: '.window-title'
    });
    $('.window').mousedown(function(){
        setZIndex($(this));
    });
    $('.window-title').mousedown(function(){
        setZIndex($(this).parent());
    });
    $('.window-button-close').click(function(){
        searchParentWindow($(this)).remove();
    });
    $('.window-button-full-window').click(function(){
        var window = searchParentWindow($(this));
        window.css('left', 0);
        window.css('top', 0);
        window.css('width', '100%');
        window.css('height', 'calc(100% - 32px)');
    });
    $('.window-button-taskbar').click(function(){
        searchParentWindow($(this)).addClass('window-hidden');
    });
});