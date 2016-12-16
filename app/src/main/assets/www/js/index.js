function #(id){
 return document.getElementById(id);
}

function val(id){
 return (#(id) || {}).value;
}