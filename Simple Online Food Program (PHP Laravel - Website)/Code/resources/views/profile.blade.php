<!DOCTYPE html>
<html style="min-height:100%" lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <link rel="stylesheet" type="text/css" href="{{ asset('/css/app.css') }}">
  <title>Document</title>
</head>
<body style = "background-image:url({{asset('/images/bg2.jpg')}});background-position:center;background-repeat:no-repeat;background-size:cover;min-height:100%">
@include('inc.navbar_home_user')
<main role="main" class="container">
		<div style="float:none;margin:0 auto;background-color:grey; opacity:0.9;height:400px" class="jumbotron col-md-7">
			<div class="text-center mb-8">
            <h1>{{$user->Nama}}</h1>
      <div class="form-label-group">
          <div style="float:left;height:100%;width:50%">
            <img class="rounded-circle" src="{{asset('/images/user/'.$user->Username.'.jpg')}}" style="width: 200px; height: 200px;"> <br>
          </div>
          <div style="float:left;height:100%;width:50%">
            <label> Username: </label><br>
            <input type="text" value="{{$user->Username}}" disabled> <br>
            Password : <br><input type="password" value="{{$user->Password}}" > <br>
            Alamat : <br><input type="text" value="{{$user->Alamat}}" > <br>
            Saldo : <br><input type="text" value="{{$user->Saldo}}" disabled> <br>
          <div>
      </div>
    </div>
  </div>
</main>


  <h3>History</h3>
  <table class="table table-dark" >
    <thead>
      <th>ID Pemesanan</th>
      <th>Penjual</th>
      <th>Tanggal Pemesanan</th>
      <th>Alamat Pengiriman</th>
      <th>Total</th>
      <th>Opsi</th>
    </thead>
    <tbody>
      @for ($i = 0; $i < count($hpesan); $i++)
        <tr>
          <td>{{$hpesan[$i]['Id_pesanan']}}</td>
          <td>{{$hpesan[$i]['Username_penjual']}}</td>
          <td>{{$hpesan[$i]['Tanggal']}}</td>
          <td>{{$hpesan[$i]['Alamat_pengiriman']}}</td>
          <td>{{$hpesan[$i]['Total']}}</td>
          <td>
              <input type="hidden" id="hiddenData[{{$i}}]" value="{{ $hpesan[$i]['dpesan'] }}">
              <button onclick="showAlert({{$i}})">Lihat Detail</button>
          </td>
        </tr>
      @endfor
    </tbody>
  </table>
</body>
</html>

<div id="data">

</div>

<script>
  function showAlert (index) {
    var data = document.getElementById('hiddenData['.concat(index,"]")).value;
    var divData = document.getElementById('data');
    var list = data.split("\n");
    var nama = [];
    var jumlah = [];
    var harga = [];
    var string = "";

    for (var i = 0; i < list.length; i++) {
      nama[i] = list[i].substring(6, list[i].indexOf(";jumlah") + 1);
      jumlah[i] = list[i].substring(list[i].indexOf(";jumlah") + 8, list[i].indexOf(";harga") + 1);
      harga[i] = list[i].substring(list[i].indexOf(";harga") + 7);
      string += nama[i] + jumlah[i] + harga[i] + "\n"; //entah kenapa tidak bisa di enter
    }
    divData.innerHTML = string;
  }
</script>
