function swaldesign(tieude,noidung,icon){
	swal(tieude, noidung, icon);
}
function swaldesigntimer(tieude,noidung,icon,timer){
	 swal({
        title: tieude,
        text: noidung,
        type: icon,
        showCancelButton: false,
        showConfirmButton: false,
        timer: timer
    });
}
function swaldesignclose(tieude,noidung,icon){
	swal({
        title: tieude,
        text: noidung,
        type: icon,
        html:true,
        showCancelButton: false,
        showConfirmButton: true,
        confirmButtonText:"Đồng ý"
        
    });
}
function swalfunction(title,messages,icon,commandRemote){
	 var previousWindowKeyDown = window.onkeydown;
	swal({
        title: title,
        text: messages,
        type: icon,
        showCancelButton: true,
        confirmButtonColor: "#DD6B55",
        confirmButtonText: "Đồng ý",
        cancelButtonText: "Không",
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',

    }, function (isConfirm) {
        window.onkeydown = previousWindowKeyDown;
        if (isConfirm) {
        	eval(commandRemote);
        	return;
        }
    });

}

