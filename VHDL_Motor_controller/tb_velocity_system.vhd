Library IEEE;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity tb_velocity_system is
end entity;

architecture testbench of tb_velocity_system is

  component input_synchronizer is
    port (
      clk : in std_logic; --100MHz
      reset : in std_logic;
      sa : in std_logic;
      sb : in std_logic;
      sa_synch : out std_logic;
      sb_synch : out std_logic
    );
  end component;

  component quadrature_decoder is
    port (
      clk : in std_logic;
      reset : in std_logic;
      sa : in std_logic;
      sb : in std_logic;
      pos_inc : out std_logic;
      pos_dec : out std_logic
    );     
  end component;

  component velocity_reader is
    port(
      mclk      : in std_logic; 
      reset     : in std_logic; 
      pos_inc   : in std_logic;
      pos_dec   : in std_logic;
      velocity  : out signed(7 downto 0) -- rpm value updated every 1/100 s 
    );
  end component;

  component seg7ctrl is
    port (
      mclk : in std_logic; --100MHz
      reset : in std_logic; --Asynchronous reset, active high
      d0 : in std_logic_vector(3 downto 0);
      d1 : in std_logic_vector(3 downto 0);
      abcdefg : out std_logic_vector(6 downto 0);
      c : out std_logic
    );
  end component;

  signal tb_sa : std_logic;
  signal tb_sb : std_logic;
  signal tb_sa_synch : std_logic;
  signal tb_sb_synch : std_logic;
  signal tb_pos_inc : std_logic;
  signal tb_pos_dec : std_logic;
  signal tb_velocity : signed(7 downto 0);
  signal tb_d0 : std_logic_vector(3 downto 0);
  signal tb_d1 : std_logic_vector(3 downto 0);
  signal tb_abcdefg : std_logic_vector(6 downto 0);
  signal tb_c : std_logic;

  signal tb_clk : std_logic := '1';
  signal tb_reset : std_logic := '0';

begin
  
  IN_SYNCH_U : input_synchronizer
    port map (
      clk => tb_clk,
      reset => tb_reset,
      sa => tb_sa,
      sb => tb_sb,
      sa_synch => tb_sa_synch,
      sb_synch => tb_sb_synch
    );

  QUAD_DECODER_U : quadrature_decoder
    port map (
      clk => tb_clk,
      reset => tb_reset,
      sa => tb_sa_synch,
      sb => tb_sb_synch,
      pos_inc => tb_pos_inc,
      pos_dec => tb_pos_dec
    );
  
  VELOCITY_U : velocity_reader
    port map(
      mclk => tb_clk,
      reset => tb_reset, 
      pos_inc => tb_pos_inc,
      pos_dec => tb_pos_dec,
      velocity => tb_velocity
    );

  SEG7CTRL_U : seg7ctrl
    port map(
      mclk => tb_clk,
      reset => tb_reset,
      d0 => tb_d0,
      d1 => tb_d1,
      abcdefg => tb_abcdefg,
      c => tb_c
    );


  tb_clk <= not tb_clk after 5 ns;
  tb_reset <= '1', '0' after 10 ns;
  tb_d0 <= std_logic_vector("abs"(tb_velocity)(7 downto 4));
  tb_d1 <= std_logic_vector("abs"(tb_velocity)(3 downto 0));

  process is
  begin
    
    --Pos inc
    for i in 1 to 100 loop
      tb_sa <= '0';
      tb_sb <= '0';
      wait for 500 ns;
      tb_sa <= '0';
      tb_sb <= '1';
      wait for 500 ns;
      tb_sa <= '1';
      tb_sb <= '1';
      wait for 500 ns;
      tb_sa <= '1';
      tb_sb <= '0';
      wait for 500 ns;
      tb_sa <= '0';
      tb_sb <= '0';          
    end loop;

  end process;
  
end architecture;
