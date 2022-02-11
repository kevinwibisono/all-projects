<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Mainpage extends CI_Controller {

	public function __construct(){
		parent::__construct();
		$this->load->library('session');
	}
	
	public function index()
	{
		$this->load->helper('cookie');
		if($this->input->cookie('logonuser') != null){
			$email = $this->input->cookie('logonuser');
			$this->session->set_userdata('userlogin', $email);
			$this->load->model('dbmanipulator');
			$role = $this->dbmanipulator->getrole($email);
			if($role == 'customer'){
				$this->dbmanipulator->deleteallkeranjang($email);
				redirect(site_url('mainpage/to_beranda'));
			}
			else if($role == 'merchant'){
				redirect(site_url('mainpage/to_merchant'));
			}
			else if($role == 'driver'){
				redirect(site_url('mainpage/to_driver'));
			}
			else if($role == 'admin'){
				redirect(site_url('mainpage/to_admin'));
			}
		}
		else{
			redirect(site_url('mainpage/to_login'));
		}
	}
	
	public function handle_login(){
		$masukprofile = false;
		$masukfood = false;
		$masukdriver = false;
		$userada = false;
		$passbenar = false;
		$currentemoney = 0;
		$currentuser = "";
		$current_alamat = "";
		$current_tag = "";
		$role = "";
		$username = $this->input->get_post("username");
		$password = $this->input->get_post("password");
		$this->load->model('dbmanipulator');
		$arruser = $this->dbmanipulator->getalluser();
		$arralamat = $this->dbmanipulator->getallalamat();
		foreach($arruser as $a){
			if(strtoupper($a->email) == strtoupper($username) && $a->email != ""){
				$userada = true;
				if(strtoupper($a->password) == strtoupper($password)){
					$passbenar = true;
					$role = $a->role;
					$current_emoney = $a->saldo;
					foreach($arralamat as $r){
						if($r->alamat != ""){
							if($r->email_cust == $a->email){
								$current_alamat = $current_alamat.";".$r->alamat;
								$current_tag = $current_tag.";".$r->tag;
							}
						}	
					}
					$current_no_telp = $a->no_telp;
					
					$this->session->set_userdata('userlogin', $a->email);
					$currentuser = $a->nama;
					$current_password =$a->password; 
				}
			}
		}
		if($userada && $passbenar){
			if($this->input->post('checkremember') != null){
				$userlogin = $this->session->userdata('userlogin');
				$this->load->helper('cookie');
				$cookie = array(
					'name' => 'logonuser',
					'value' => $userlogin,
					'expire' => 10800
				);
				$this->input->set_cookie($cookie);
				echo "Cookie";
			}	
			$data['error'] = "";
			if($role == 'customer'){
				$masukprofile = true;
			}
			else if($role == 'merchant'){
				$masukfood = true;
			}
			else if($role == 'driver'){
				$masukdriver = true;
			}
			else if($role == 'admin'){
				$masukadmin = true;
			}
		}
		else{
			if(!$userada){
				
				$this->session->set_flashdata('errorlogin', "User dengan email tersebut tidak ditemukan");
			}
			else if(!$passbenar){
				
				$this->session->set_flashdata('errorlogin', "Password yang diinputkan salah");
			}
		}
		if($masukprofile){
			//masuk ke hlm beranda
			redirect(site_url('mainpage/to_beranda'));
		}
		else if($masukfood){
			//masuk ke hlm master makanan
			redirect(site_url('mainpage/to_merchant'));
		}
		else if($masukdriver){
			redirect(site_url("mainpage/to_driver"));
		}
		else if($masukadmin){
			redirect(site_url("mainpage/to_admin"));
		}
		else{
			//tidak masuk semua nya
			redirect(site_url("mainpage/to_login"));
		}
		
	}
	
	public function to_beranda(){
		$this->load->model('dbmanipulator');
		$video = $this->dbmanipulator->getvideocount();
		if($video > 0){
			$videos = $this->dbmanipulator->getallvideo();
			$random = rand(0, $video-1);
			$idx = -1;
			foreach($videos as $v){
				$idx += 1;
				if($idx == $random){
					$data['ads'] = $v->nama;
				}
			}
		}
		else{
			$data['ads'] = '';
		}
		$data['alamat'] = $this->dbmanipulator->getallalamat();
		$data['merchant'] = $this->dbmanipulator->getsellingmerchant();
		$data['current_user'] = $this->dbmanipulator->getNamaMerch($this->session->userdata('userlogin'))->nama;
		$data['current_emoney'] = $this->dbmanipulator->getemoney($this->session->userdata('userlogin'));
		$this->load->helper('html');
		$this->load->helper("form");
		$this->load->view('beranda', $data);
	}
	
	public function to_merchant(){
		if($this->session->flashdata('errorFood') != null){
			$data['error'] = $this->session->flashdata('errorFood');
		}
		else{
			$data['error'] = "";
		}
		$this->load->model('dbmanipulator');
		$data['makanan'] = $this->dbmanipulator->getallmakanan();
		$data['current_user'] = $this->session->userdata('userlogin');
		$this->load->helper('html');
		$this->load->helper("form");
		$data['upfood'] = "";
		$this->load->view('mastermakanan', $data);
	}

	public function to_admin(){
		if($this->session->flashdata('errorVideo') != null){
			$data['error'] = $this->session->flashdata('errorVideo');
		}
		else{
			$data['error'] = "";
		}
		$this->load->model('dbmanipulator');
		$data['video'] = $this->dbmanipulator->getallvideo();
		$this->load->helper('form');
		$this->load->helper('html');
		$this->load->view('adminpage', $data);
	}
	
	public function to_regis(){
		
		if($this->session->flashdata('errorregis') == null){
			$data['error'] = "";
		}
		else{
			$data['error'] = $this->session->flashdata('errorregis');
		}
		//$this->load->helper("url");
		$this->load->helper('html');
		$this->load->helper('form');
		$this->load->view("register", $data);
	}
	
	public function handle_register(){
		if($this->input->post('role') != null){
			$role = $this->input->post('role');
		}
		$registervalid = false;
		$alamatterisi = false;
		$jumlahalamat = $this->input->get_post("HiddenJumlah");
		$this->load->library('form_validation');
		$this->form_validation->set_rules('nama', 'nama', 'required');
		$this->form_validation->set_message('required', 'Field %s wajib diisi');
		$this->form_validation->set_rules('nomor', 'no telp', 'required|numeric|min_length[10]|max_length[16]');
		$this->form_validation->set_message('numeric', 'Field %s hanya dapat diisi oleh angka');
		$this->form_validation->set_message('min_length', 'Jumlah karakter %s minimal 10');
		$this->form_validation->set_message('max_length', 'Jumlah karakter %s maksimal 16');
		$this->form_validation->set_message('valid_email', 'Format email tidak sesuai');
		$this->form_validation->set_rules('password', 'password', 'callback_same_password');
		$this->form_validation->set_rules('username', 'email', 'required|valid_email|callback_unique_email');
		$this->form_validation->set_rules('captcha', 'konfirmasi', 'required');
		$this->form_validation->set_rules('role', 'role', 'required');
		for($i=0;$i<=$jumlahalamat;$i++){
			if($this->input->get_post("alamat[$i]") != "" && $this->input->get_post("tag[$i]") != ""){
				$alamatterisi = true;
			}
		}
		if($role == 'driver'){
			$FileName = str_replace('.', '_', $this->input->get_post("username")).".jpg";
			$this->load->library('upload');
			$config = array(
				'upload_path' => './assets/images',
				'allowed_types' => 'png|jpg',
				'overwrite' => true,
				'file_name' => $FileName
			);
			$this->upload->initialize($config);
			if($this->form_validation->run() == true && $alamatterisi && $this->upload->do_upload('UploadPP')){
				$registervalid = true;
				$data['error'] = "";
				$data['color'] = "green";
				for($i=0;$i<=$jumlahalamat;$i++){
					if($this->input->get_post("alamat[$i]") != "" && $this->input->get_post("tag[$i]") != ""){
						//insert ke alamat
						$this->load->model('dbmanipulator');
						$this->dbmanipulator->insertAlamat($this->input->get_post("alamat[$i]"), $this->input->get_post("tag[$i]"), $this->input->get_post("username"));
					}
				}
				$this->dbmanipulator->insertUser($this->input->get_post("username"), $this->input->get_post("nama"), $this->input->get_post("nomor"), $this->input->get_post("password"), $this->input->get_post("role"), $FileName);
				//$this->load->helper('url');
				$this->load->helper('html');
				$this->load->helper('form');
				$this->load->view('login', $data);
			}
			else{
				
				if(!$alamatterisi){
					$this->session->set_flashdata('errorregis', validation_errors()."<br>Minimal satu alamat harus diisi");
				}
				else if($this->form_validation->run() == false){
					$this->session->set_flashdata('errorregis', validation_errors());
				}
				else if(!$this->upload->do_upload('UploadPP')){
					$this->session->set_flashdata('errorregis', $this->upload->display_errors());
				}
				redirect(site_url("mainpage/to_regis"));
			}
		}
		else{
			if($this->form_validation->run() == true && $alamatterisi){
				$registervalid = true;
				$data['error'] = "";
				$data['color'] = "green";
				for($i=0;$i<=$jumlahalamat;$i++){
					if($this->input->get_post("alamat[$i]") != "" && $this->input->get_post("tag[$i]") != ""){
						//insert ke alamat
						$this->load->model('dbmanipulator');
						$this->dbmanipulator->insertAlamat($this->input->get_post("alamat[$i]"), $this->input->get_post("tag[$i]"), $this->input->get_post("username"));
					}
				}
				$this->dbmanipulator->insertUser($this->input->get_post("username"), $this->input->get_post("nama"), $this->input->get_post("nomor"), $this->input->get_post("password"), $this->input->get_post("role"));
				//$this->load->helper('url');
				$this->load->helper('html');
				$this->load->helper('form');
				$this->load->view('login', $data);
			}
			else{
				
				if(!$alamatterisi){
					$this->session->set_flashdata('errorregis', validation_errors()."<br>Minimal satu alamat harus diisi");
				}
				else{
					$this->session->set_flashdata('errorregis', validation_errors());
				}
				redirect(site_url("mainpage/to_regis"));
			}
		}
		
	}
	
	public function to_login(){
		$this->load->helper('cookie');
		if($this->input->cookie('logonuser') != null){
			delete_cookie('logonuser');
		}
		if($this->session->flashdata('errorlogin') == null){
			$data['error'] = "";
		}
		else{
			$data['error'] = $this->session->flashdata('errorlogin');
		}
		$this->session->unset_userdata('choosenmerch');
		$this->load->model('dbmanipulator');
		$this->dbmanipulator->deleteallkeranjang("");
		//$this->load->helper("url");
		$this->load->helper("html");
		$this->load->helper("form");
		$this->load->view("login", $data);
	}
	
	public function unique_email($str){
		$this->load->model('dbmanipulator');
		$unik = true;
		$arremail = $this->dbmanipulator->getalluser();
		foreach($arremail as $r){
			if($str == $r->email){
				$unik = false;
			}
		}
		if($unik || $str == ""){
			return true;
		}
		else{
			$this->form_validation->set_message('unique_email', 'Email sudah digunakan, mohon inputkan email lain');
			return false;
		}
	}
	
	public function same_password($str){
		$cpass = $this->input->get_post('cpassword');
		if($str == $cpass){
			return true;
		}
		else{
			$this->form_validation->set_message('same_password', 'Field %s harus bernilai sama dengan confirm password');
			return false;
		}
	}
	
	public function handle_insert_food(){
		$FileName = str_replace(' ', '_', $this->input->post('makanan')).str_replace('.', '_', $this->session->userdata('userlogin')).".jpg";
		$this->load->library('upload');
		$config = array(
			'upload_path' => './assets/images',
			'allowed_types' => 'png|jpg',
			'overwrite' => true,
			'file_name' => $FileName
		);
		$this->upload->initialize($config);
		$this->load->library('form_validation');
		$this->form_validation->set_message('required', 'Field %s wajib diisi');
		$this->form_validation->set_rules('makanan', 'nama makanan', 'required|callback_unique_makanan');
		$this->form_validation->set_rules('harga', 'harga', 'required|numeric');
		$this->form_validation->set_message('numeric', 'Field %s hanya dapat diisi oleh angka');
		$this->load->model('dbmanipulator');
		if($this->form_validation->run() == true && $this->upload->do_upload('UploadGambar')){
			$arr = $this->upload->data();
			$data['error'] = "";		
			$this->dbmanipulator->insertMakanan($this->input->get_post("makanan"), $this->input->get_post("harga"), $this->input->get_post("deskripsi"),$this->session->userdata('userlogin'), 'Enable', $FileName);
			redirect(site_url('mainpage/to_merchant'));
		}
		else if($this->form_validation->run() == false){
			$this->session->set_flashdata('errorFood', validation_errors());
			redirect(site_url('mainpage/to_merchant'));
		}
		else{
			if(strpos($this->upload->display_errors(), 'You did not select a file to upload.') != null){
				$FileName = "defaultfood.png";
				$data['error'] = "";		
				$this->dbmanipulator->insertMakanan($this->input->get_post("makanan"), $this->input->get_post("harga"), $this->input->get_post("deskripsi"),$this->session->userdata('userlogin'), 'Enable', $FileName);
				redirect(site_url('mainpage/to_merchant'));
			}
			else{
				$this->session->set_flashdata('errorFood', $this->upload->display_errors());
				redirect(site_url('mainpage/to_merchant'));
			}
		}
	}
	
	public function unique_makanan($str){
		$this->load->model('dbmanipulator');
		$user = $this->input->post('HiddenUser');
		$unik = true;
		$arrfood = $this->dbmanipulator->getallmakanan();
		foreach($arrfood as $r){
			if($str == $r->nama && $r->email_merch == $user){
				$unik = false;
			}
		}
		if($unik || $str == ""){
			return true;
		}
		else{
			$this->form_validation->set_message('unique_makanan', 'Nama makanan sudah tersedia di daftar menu, mohon inputkan makanan lain');
			return false;
		}
	}
	
	public function updatefood(){
		$FileName = str_replace(' ', '_', $this->input->post('NewNama')).str_replace('.', '_', $this->session->userdata('userlogin')).".jpg";
		$this->load->library('upload');
		$config = array(
			'upload_path' => './assets/images',
			'allowed_types' => 'png|jpg',
			'overwrite' => true,
			'file_name' => $FileName
		);
		$this->upload->initialize($config);
		$this->load->library('form_validation');
		$this->load->model('dbmanipulator');
		$this->form_validation->set_message('required', 'Field %s wajib diisi');
		$this->form_validation->set_rules('NewNama', 'nama makanan', 'required|callback_unique_makanan_update');
		$this->form_validation->set_rules('NewHarga', 'harga', 'required|numeric');
		$this->form_validation->set_message('numeric', 'Field %s hanya dapat diisi oleh angka');
		if($this->form_validation->run() == true && $this->upload->do_upload('NewUploadGambar')){
			$nama = $this->input->post('HiddenUpMakanan');
			$newnama = $this->input->post('NewNama');
			$newharga = $this->input->post('NewHarga');
			$newdeskripsi = $this->input->post('NewDeskripsi');
			$penjual = $this->session->userdata('userlogin');
			$this->dbmanipulator->updatefood($nama, $newnama, $newharga, $newdeskripsi, $penjual, $FileName);
			redirect(site_url('mainpage/to_merchant'));
		}
		else if($this->form_validation->run() == false){
			$this->session->set_flashdata('errorFood', validation_errors());
			redirect(site_url('mainpage/to_merchant'));
		}
		else{
			if(strpos($this->upload->display_errors(), 'You did not select a file to upload.') != null){
				$FileName = "defaultfood.png";
				$nama = $this->input->post('HiddenUpMakanan');
				$newnama = $this->input->post('NewNama');
				$newharga = $this->input->post('NewHarga');
				$newdeskripsi = $this->input->post('NewDeskripsi');
				$penjual = $this->session->userdata('userlogin');
				$this->dbmanipulator->updatefood($nama, $newnama, $newharga, $newdeskripsi, $penjual, $FileName);
				redirect(site_url('mainpage/to_merchant'));
			}
			else{
				$this->session->set_flashdata('errorFood', $this->upload->display_errors());
				redirect(site_url('mainpage/to_merchant'));
			}
		}
	}
	
	public function disablefood(){
		$nama = $this->input->post('HiddenMakanan');
		$penjual = $this->session->userdata('userlogin');
		$this->load->model('dbmanipulator');
		$this->dbmanipulator->disablefood($nama, $penjual);
		$data['current_user'] = $this->session->userdata('userlogin');
		$this->load->helper('html');
		$this->load->helper('form');
		$data['error'] = "";
		$data['makanan'] = $this->dbmanipulator->getallmakanan();
		$data['upfood'] = $this->input->get_post("HiddenUpMakanan");
		$this->load->view('mastermakanan', $data);
		
	}
	
	public function deletefood(){
		$nama = $this->input->post('HiddenMakanan');
		$penjual = $this->session->userdata('userlogin');
		$this->load->model('dbmanipulator');
		$this->dbmanipulator->deletefood($nama, $penjual);
		$data['current_user'] = $this->session->userdata('userlogin');
		$this->load->helper('html');
		$this->load->helper('form');
		$data['error'] = "";
		$data['makanan'] = $this->dbmanipulator->getallmakanan();
		$data['upfood'] = $this->input->get_post("HiddenUpMakanan");
		$this->load->view('mastermakanan', $data);
	}
	
	public function unique_makanan_update($str){
		$namalama = $this->input->post('HiddenUpMakanan');
		$user = $this->session->userdata('userlogin');
		if($str == $namalama){
			return true;
		}
		else{
			$this->load->model('dbmanipulator');
			$unik = true;
			$arrfood = $this->dbmanipulator->getallmakanan();
			foreach($arrfood as $r){
				if($str == $r->nama && $r->email_merch == $user){
					$unik = false;
				}
			}
			if($unik || $str == ""){
				return true;
			}
			else{
				$this->form_validation->set_message('unique_makanan_update', 'Nama makanan sudah tersedia di daftar menu, mohon inputkan makanan lain');
				return false;
			}
		}
	}
	
	public function to_detail(){
		$email = $this->session->userdata('userlogin');
		$emailMerch = "";
		if($this->session->userdata('choosenmerch') != null){
			$emailMerch = $this->session->userdata('choosenmerch');
		}
		else{
			$emailMerch = $this->input->post('HiddenEmailMerchant');
			$this->session->set_userdata('choosenmerch', $emailMerch);
		}
		$this->load->model('dbmanipulator');
		
		if($this->session->flashdata('errorkeranjang') == null){
			$data['error'] = "";
		}
		else{
			$data['error'] = $this->session->flashdata('errorkeranjang');
		}
		$data['current_merchant'] = $this->dbmanipulator->getNamaMerch($emailMerch)->nama;
		$data['current_email'] = $email; 
		$data['current_email_merchant'] = $emailMerch; 
		$data['alamat'] = $this->dbmanipulator->getallalamat($emailMerch);
		$data['makanan'] = $this->dbmanipulator->getenabledmakanan($emailMerch);
		$this->load->helper('html');
		$this->load->helper('form');
		$this->load->view('detail', $data);
		
	}
	
	public function add_keranjang(){
		$this->load->model('dbmanipulator');
		$gambar = $this->input->post('HiddenGambar');
		$makanan = $this->input->post('HiddenNama');
		$harga = $this->input->post('HiddenHarga');
		$jumlah = $this->input->post('Jumlah');
		$email = $this->session->userdata('userlogin');
		$emailMerch = $this->input->post('HiddenEmailMerchant');
		$this->load->library('form_validation');
		$this->form_validation->set_rules('Jumlah', 'quantity', 'required|numeric|greater_than[0]');
		$this->form_validation->set_message('numeric', 'Field %s hanya dapat diisi oleh angka');
		$this->form_validation->set_message('greater_than', '%s harus bernilai minimal 1');
		if($this->form_validation->run() == true){
			$this->dbmanipulator->insertKeranjang($makanan, $harga, $jumlah, $email, $gambar);
			$data['error'] = "";
		}
		else{
			
			$this->session->set_flashdata('errorkeranjang', validation_errors());
			$data['error'] = validation_errors();
			redirect(site_url("mainpage/to_detail"));
		}
		$data['current_merchant'] = $this->dbmanipulator->getNamaMerch($emailMerch)->nama;
		$data['current_email'] = $email; 
		$data['current_email_merchant'] = $emailMerch; 
		$data['alamat'] = $this->dbmanipulator->getallalamat($emailMerch);
		$data['makanan'] = $this->dbmanipulator->getenabledmakanan($emailMerch);
		$this->load->helper('html');
		$this->load->helper('form');
		$this->load->view('detail', $data);
	}
	
	public function to_checkout(){
		$data['current_email'] = $this->session->userdata('userlogin');
		$data['current_email_merchant'] = $this->input->post('HiddenEmailMerchant');
		$data['chosen_address_merchant'] = $this->input->post('HiddenAddressMerchant');
		$this->load->model('dbmanipulator');
		$data['current_emoney'] = $this->dbmanipulator->getemoney($this->session->userdata('userlogin'));
		$data['keranjang'] = $this->dbmanipulator->getallkeranjang($data['current_email']);
		$data['alamat'] = $this->dbmanipulator->getallalamat();
		$this->load->helper('html');
		$this->load->helper('form');
		$this->load->view('checkout', $data);
	}
	
	public function inc_makanan(){
		$makanan = $this->input->post('HiddenMakanan');
		$email = $this->session->userdata('userlogin');
		$jumlah = $this->input->post('HiddenJumlah');
		$this->load->model('dbmanipulator');
		$data['error'] = "";
		$data['current_email'] = $email;
		$data['current_emoney'] = $this->dbmanipulator->getemoney($this->session->userdata('userlogin'));
		$data['current_email_merchant'] = $this->input->post('HiddenEmailMerchant');
		$data['chosen_address_merchant'] = $this->input->post('HiddenAddressMerchant');
		$this->dbmanipulator->incMakanan($makanan, $email, $jumlah);
		$data['keranjang'] = $this->dbmanipulator->getallkeranjang($data['current_email']);
		$data['alamat'] = $this->dbmanipulator->getallalamat();
		$this->load->helper('html');
		$this->load->helper('form');
		$this->load->view('checkout', $data);
	}
	
	public function dec_makanan(){
		$makanan = $this->input->post('HiddenMakanan');
		$email = $this->session->userdata('userlogin');
		$jumlah = $this->input->post('HiddenJumlah');
		$this->load->model('dbmanipulator');
		$data['error'] = "";
		$data['current_email'] = $email;
		$data['current_emoney'] = $this->dbmanipulator->getemoney($this->session->userdata('userlogin'));
		$data['current_email_merchant'] = $this->input->post('HiddenEmailMerchant');
		$data['chosen_address_merchant'] = $this->input->post('HiddenAddressMerchant');
		$this->dbmanipulator->decMakanan($makanan, $email, $jumlah);
		$data['keranjang'] = $this->dbmanipulator->getallkeranjang($data['current_email']);
		$data['alamat'] = $this->dbmanipulator->getallalamat();
		$this->load->helper('html');
		$this->load->helper('form');
		$this->load->view('checkout', $data);
	}
	
	public function do_transaction(){
		$email = $this->session->userdata('userlogin');
		$emailMerchant = $this->input->post('HiddenEmailMerchant');
		$addressMerchant = $this->input->post('HiddenAddressMerchant');
		$tag = $this->input->post('HiddenTag');
		$saldo = $this->input->post('HiddenEMoney');
		$total = $this->input->post('total');
		$this->load->model('dbmanipulator');
		if($saldo >= $total){
			//lakukan transaksi
			$this->dbmanipulator->do_transaction($email, $emailMerchant, $addressMerchant, $tag, $total);
			$this->dbmanipulator->deleteallkeranjang($email);
			$this->to_login();
		}
		else{
			//gagal, kembali ke login dan batalkan transaksi
			$this->dbmanipulator->deleteallkeranjang($email);
			$this->to_login();
		}
	}
	
	public function to_profile(){
		$email = $this->session->userdata('userlogin');
		$this->load->model('dbmanipulator');
		$data['current_user'] = $this->dbmanipulator->getNamaMerch($email)->nama;
		$data['current_alamat'] = $this->dbmanipulator->getallalamat($email);
		$data['current_email'] = $this->dbmanipulator->getalluser($email)->email;
		$data['current_emoney'] = $this->dbmanipulator->getalluser($email)->saldo;
		$data['current_no_telp'] = $this->dbmanipulator->getalluser($email)->no_telp;
		$data['htrans'] = $this->dbmanipulator->getallhtrans($email);
		$data['dtrans'] = $this->dbmanipulator->getalldtrans();
		$this->load->helper("html");
		$this->load->helper("form");
		$this->load->view("profile", $data);
	}
	
	public function to_topup(){
		$this->load->helper("html");
		$this->load->helper("form");
		$this->load->view("topup");
	}
	
	public function do_topup(){
		$email = $this->session->userdata('userlogin');
		$tambah = $this->input->post('Adding');
		$this->load->model('dbmanipulator');
		$this->dbmanipulator->updatesaldo($email, $tambah);
		redirect(site_url("mainpage/to_login"));
	}
	
	public function to_driver(){
		$email = $this->session->userdata('userlogin');
		$this->load->model('dbmanipulator');
		$jum = $this->dbmanipulator->getnumdelivering($email);
		$data['current_user'] = $this->dbmanipulator->getNamaMerch($email)->nama;
		$data['current_emoney'] = $this->dbmanipulator->getemoney($email);
		$data['current_email'] = $email;
		$data['htrans'] = $this->dbmanipulator->getallhtrans();
		$data['dtrans'] = $this->dbmanipulator->getalldtrans();
		$this->load->helper("html");
		$this->load->helper("form");
		if($jum <= 0){
			$this->load->view("driverpage", $data);
		}
		else{
			$this->load->view("driverdelivering", $data);
		}
	}
	
	public function acceptdelivery(){
		$idtrans = $this->input->post('HiddenIDTrans');
		$driver = $this->session->userdata('userlogin');
		$this->load->model('dbmanipulator');
		$this->dbmanipulator->acceptdelivery($idtrans, $driver);
		redirect(site_url('mainpage/to_driver'));
	}
	
	public function pengiriman(){
		$idtrans = $this->input->post('HiddenIDTrans');
		$driver = $this->session->userdata('userlogin');
		$this->load->model('dbmanipulator');
		$this->dbmanipulator->delivered($idtrans, $driver);
		redirect(site_url('mainpage/to_driver'));
	}
	
	public function tariksaldo(){
		if($this->session->flashdata('errorTarik') != null){
			$data['error'] = $this->session->flashdata('errorTarik');
		}
		else{
			$data['error'] = "";
		}
		$email = $this->session->userdata('userlogin');
		$this->load->model('dbmanipulator');
		$data['current_saldo'] = $this->dbmanipulator->getemoney($email);
		$this->load->helper("html");
		$this->load->helper("form");
		$this->load->view("tarik", $data);
	}
	
	public function do_penarikan(){
		$this->load->model('dbmanipulator');
		$email = $this->session->userdata('userlogin');
		$saldo = $this->dbmanipulator->getemoney($email);
		$tarik = $this->input->post('Jumlah');
		$this->load->model('dbmanipulator');
		if($saldo >= $tarik){
			$tarik = $tarik * -1;
			$this->dbmanipulator->updatesaldo($email, $tarik);
			redirect(site_url('mainpage/to_login'));
		}
		else{
			$this->session->set_flashdata('errorTarik', 'Gagal! Saldo tidak cukup');
			redirect(site_url('mainpage/tariksaldo'));
		}
	}
	
	public function profile_driver(){
		$email = $this->session->userdata('userlogin');
		$this->load->model('dbmanipulator');
		$data['current_user'] = $this->dbmanipulator->getNamaMerch($email)->nama;
		$data['current_alamat'] = $this->dbmanipulator->getallalamat($email);
		$data['current_email'] = $this->dbmanipulator->getalluser($email)->email;
		$data['current_emoney'] = $this->dbmanipulator->getalluser($email)->saldo;
		$data['current_no_telp'] = $this->dbmanipulator->getalluser($email)->no_telp;
		$data['htrans'] = $this->dbmanipulator->getallhtrans();
		$data['dtrans'] = $this->dbmanipulator->getalldtrans();
		$this->load->helper("html");
		$this->load->helper("form");
		$this->load->view("profile_driver", $data);
	}
	
	public function deletegambar(){
		$this->load->model('dbmanipulator');
		$this->dbmanipulator->deletegambarfood($this->input->post('HiddenUpMakananForDelete'),$this->session->userdata('userlogin'));
		redirect(site_url('mainpage/to_merchant'));
	}

	public function tambahvideo(){
		$this->load->library('upload');
		$config = array(
			'upload_path' => './assets/videos',
			'allowed_types' => 'mp4',
			'overwrite' => true
		);
		$this->upload->initialize($config);
		if($this->upload->do_upload('UploadVideo')){
			$arr = $this->upload->data();
			$this->load->model('dbmanipulator');
			$this->dbmanipulator->insertvideo($arr['file_name']);
			redirect(site_url('mainpage/to_admin'));
		}
		else{
			$this->session->set_flashdata('errorVideo', $this->upload->display_errors());
			redirect(site_url('mainpage/to_admin'));
		}
	}
	
	public function deletevideo(){
		$nama = $this->input->post('HiddenAdsName');
		$this->load->model('dbmanipulator');
		$this->dbmanipulator->deletevideo($nama);
		redirect(site_url('mainpage/to_admin'));
	}
}
