<?php
	
class Dbmanipulator extends CI_Model{
	public function __construct(){
		parent::__construct();
		$this->load->database();
	}
	
	public function insertUser($email, $nama, $telepon, $password, $role, $gambar = ""){
		if($role == 'driver'){
			$saldo = 0;
		}
		else{
			$saldo = 100000;
		}
		$this->db->query("INSERT INTO user values('$email', '$nama', '$telepon', '$password', $saldo, '$role', '$gambar')");
	}
	
	public function getalluser($role = ""){
		if($role == ""){
			return $this->db->from('user')->get()->result();
		}
		else if($role == 'merchant' || $role == 'customer'){
			//get user by role
			return $this->db->from('user')->where('role', $role)->get()->result();
		}
		else{
			//get user by email
			return $this->db->from('user')->where('email', $role)->get()->row();
		}
	}
	
	public function insertAlamat($alamat, $tag, $email){
		$this->db->query("INSERT INTO alamat values('$alamat', '$tag', '$email')");
	}
	
	public function getallalamat($email = ""){
		if($email == ""){
			return $this->db->from('alamat')->get()->result();
		}
		else{
			return $this->db->from('alamat')->where('email_cust', $email)->get()->result();
		}
	}
	
	public function getalltag($email = ""){
		if($email == ""){
			return $this->db->from('alamat')->get()->result();
		}
		else{
			return $this->db->select('tag')->from('alamat')->where('email_cust', $email)->get()->result();
		}
	}
	
	public function insertMakanan($nama, $harga, $deskripsi, $email, $status, $gambar){
		$this->db->query("INSERT INTO makanan values('$nama', $harga, '$deskripsi', '$email', '$status', '$gambar')");
	}
	
	public function getallmakanan(){
		return $this->db->from('makanan')->get()->result();
	}
	
	//ada update dan delete serta disable
	
	public function updatefood($nama, $namaedited, $harga, $deksripsi, $penjual, $gambar){
		$this->db->query("UPDATE makanan set nama = '$namaedited', harga = $harga, deskripsi = '$deksripsi', gambar = '$gambar' WHERE nama = '$nama' and email_merch = '$penjual'");
	}
	
	public function disablefood($nama = "", $penjual){
		$row = $this->db->query("SELECT * FROM makanan WHERE nama = '$nama' and email_merch = '$penjual'")->row();
		if($row->status == 'Enable'){
			$this->db->query("UPDATE makanan set status = 'Disable' WHERE nama = '$nama' and email_merch = '$penjual'");
		}
		else{
			$this->db->query("UPDATE makanan set status = 'Enable' WHERE nama = '$nama' and email_merch = '$penjual'");
		}
	}
	
	public function deletefood($nama = "", $penjual){
		$string = $nama.'Deleted';
		$this->db->query("UPDATE makanan set nama='$string' WHERE nama = '$nama' and email_merch = '$penjual'");
	}
	
	public function getNamaMerch($email){
		return $this->db->select('nama')->from('user')->where('email', $email)->get()->row();
	}
	
	public function getallkeranjang($email = ""){
		if($email == ""){
			return $this->db->from('keranjang')->get()->result();
		}
		else{
			return $this->db->from('keranjang')->where('email_cust', $email)->get()->result();
		}
	}
	
	public function insertKeranjang($makanan, $harga, $jumlah, $email, $gambar){
		if($this->db->from('keranjang')->where('makanan', $makanan)->where('email_cust', $email)->count_all_results() > 0){
			//makanan sdh ada hanya menambahkan jumlahnya
			$jumlahLama = $this->db->select('jumlah')->from('keranjang')->where('makanan', $makanan)->where('email_cust', $email)->get()->row()->jumlah;
			$data = array('jumlah' => $jumlahLama + $jumlah);
			$this->db->where('makanan' , $makanan)->where('email_cust' , $email)->update('keranjang', $data);
		}
		else{
			$data = array(
						'makanan' => $makanan,
						'harga' => $harga,
						'jumlah' => $jumlah,
						'subtotal' => $harga * $jumlah,
						'email_cust' => $email,
						'gambar' => $gambar
					);
			$this->db->insert('keranjang', $data);
		}
	}
	
	public function incMakanan($makanan, $email, $jumlah){
		$data = array(
			'jumlah' => $jumlah + 1
		);
		
		$this->db->where('makanan' , $makanan);
		$this->db->where('email_cust' , $email);
		$this->db->update('keranjang', $data);
	}
	
	public function decMakanan($makanan, $email, $jumlah){
		if($jumlah >= 1){
			$data = array(
				'jumlah' => $jumlah - 1
			);
			$this->db->where('makanan' , $makanan);
			$this->db->where('email_cust' , $email);
			$this->db->update('keranjang', $data);
		}
	}
	
	public function getsellingmerchant(){
		$emailmerch = $this->db->select('email_merch')->from('makanan')->not_like('nama', 'Deleted')->where('status', 'Enable')->get()->result();
		$email = array();
		foreach($emailmerch as $e){
			array_push($email, $e->email_merch);
		}
		if(count($email) > 0){
			return $this->db->from('user')->where_in('email', $email)->get()->result();
		}
		else{
			return $this->db->from('user')->where('email', '0000000000')->get()->result();
		}
	}
	
	public function getenabledmakanan($email){
		return $this->db->from('makanan')->where('email_merch', $email)->where('status', 'Enable')->get()->result();
	}
	
	public function getemoney($email){
		return $this->db->from('user')->where('email', $email)->get()->row()->saldo;
	}
	
	public function getrole($email){
		return $this->db->from('user')->where('email', $email)->get()->row()->role;
	}
	
	public function deleteallkeranjang($email){
		$this->db->where('email_cust', $email);
		$this->db->delete('keranjang');
	}
	
	public function do_transaction($email, $emailMerchant, $addressMerchant, $tag, $total){
		//insert htrans
		$nama = $this->getNamaMerch($emailMerchant)->nama;
		$alamat = $this->db->from('alamat')->where('email_cust', $email)->where('tag', $tag)->get()->row()->alamat;
		$id = $this->db->select_max('id_trans', 'maxId')->from('htrans')->get()->row()->maxId;
		$id += 1;
		$data = array(
			'alamat_dari' => $addressMerchant,
			'alamat_tujuan' => $alamat,
			'email_merchant' => $emailMerchant,
			'nama_mechant' => $nama,
			'total' => $total, 
			'email_cust' => $email,
			'nama_cust' => $this->getNamaMerch($email)->nama,
			'id_trans' => $id, 
			'status' => 'Menunggu driver'
		);
		
		$this->db->insert('htrans', $data);
		
		
		//insert dtrans
		$keranjang = $this->getallkeranjang($email);
		foreach($keranjang as $k){
			if($k->jumlah > 0){
				$datas = array(
					'makanan' => $k->makanan,
					'harga' => $k->harga,
					'jumlah' => $k->jumlah,
					'id_trans' => $id,
					'gambar' => $k->gambar
				);
				
				$this->db->insert('dtrans', $datas);
			}
		}
		
		
	}
	
	public function getallhtrans($email=""){
		if($email == ""){
			return $this->db->from('htrans')->get()->result();
		}
		else{
			return $this->db->from('htrans')->where('email_cust', $email)->get()->result();
		}
	}
	
	public function getalldtrans(){
		return $this->db->from('dtrans')->get()->result();
	}
	
	public function updatesaldo($email, $tambah){
		$saldo = $this->db->select('saldo')->from('user')->where('email', $email)->get()->row()->saldo;
		$data = array(
			'saldo' => $saldo + $tambah
		);
		$this->db->where('email' , $email);
		$this->db->update('user', $data);
	}
	
	public function getavailablehtrans(){
		return $this->db->from('htrans')->where('status', "Menunggu driver")->get()->result();
	}
	
	public function acceptdelivery($id, $driver){
		$gbrdriver = $this->getgambardriver($driver);
		$namadriver = $this->getNamaMerch($driver)->nama;
		$str = date("Y/m/d")." ".(date("h")+5).":".date("i:sa");
		$data = array(
			'tglpengiriman' => $str,
			'status' => 'Pengiriman',
			'email_driver' => $driver,
			'nama_driver' => $namadriver,
			'gambar_driver' => $gbrdriver
		);
		$this->db->where('id_trans' , $id);
		$this->db->update('htrans', $data);
	}
	
	public function delivered($id, $driver){
		$str = date("Y/m/d")." ".(date("h")+5).":".date("i:sa");
		$data = array(
			'tglsampai' => $str,
			'status' => 'Sampai'
		);
		$this->db->where('id_trans' , $id);
		$this->db->update('htrans', $data);
		
		//mengurangi saldo user
		$total = $this->db->select('total')->from('htrans')->where('id_trans', $id)->get()->row()->total;
		$email = $this->db->select('email_cust as email')->from('htrans')->where('id_trans', $id)->get()->row()->email;
		$saldo = $this->db->select('saldo')->from('user')->where('email', $email)->get()->row()->saldo;
		$data = array(
			'saldo' => $saldo - $total
		);
		$this->db->where('email' , $email);
		$this->db->update('user', $data);
		
		//menambah saldo driver
		$saldo = $this->db->select('saldo')->from('user')->where('email', $driver)->get()->row()->saldo;
		$data = array(
			'saldo' => $saldo + 10000
		);
		$this->db->where('email' , $driver);
		$this->db->update('user', $data);
		
		//menambah saldo merchant
		$total = $this->db->select('total')->from('htrans')->where('id_trans', $id)->get()->row()->total;
		$email = $this->db->select('email_merchant as email')->from('htrans')->where('id_trans', $id)->get()->row()->email;
		$saldo = $this->db->select('saldo')->from('user')->where('email', $email)->get()->row()->saldo;
		$data = array(
			'saldo' => $saldo + $total
		);
		$this->db->where('email' , $email);
		$this->db->update('user', $data);
	}
	
	public function getnumdelivering($driver){
		return $this->db->select("count(id_trans) as jumlah")->from('htrans')->where('status', 'Pengiriman')->where('email_driver', $driver)->get()->row()->jumlah;
	}
	
	public function deletegambarfood($nama, $email_merch){
		$data = array(
			'gambar' => 'defaultfood.png'
		);
		$this->db->where('nama', $nama);
		$this->db->where('email_merch', $email_merch);
		$this->db->update('makanan', $data);
	}
	
	public function getmakanan($nama, $email_merch){
		return $this->db->from('makanan')->where('nama', $nama)->where('email_merch', $email_merch)->get()->row();
	}
	
	public function getallvideo(){
		return $this->db->from('video')->get()->result();
	}
	
	public function insertvideo($video){
		$data = array(
			'nama' => $video
		);
		
		$this->db->insert('video', $data);
	}
	
	public function getvideocount(){
		return $this->db->select("count(nama) as jumlah")->from('video')->get()->row()->jumlah;
	}
	
	public function deletevideo($nama){
		$this->db->where('nama', $nama);
		$this->db->delete('video');
	}
	
	public function getgambardriver($driver){
		return $this->db->from('user')->where('email', $driver)->get()->row()->gambar;
	}
}

?>