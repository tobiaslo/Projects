Library IEEE;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity system is
  port (
    clk : in std_logic;
    reset : in std_logic;
    sa_in : in std_logic;
    sb_in : in std_logic;
    dir_out : out std_logic;
    en_out : out std_logic;
    abcdefg_out : out std_logic_vector(6 downto 0);
    c_out : out std_logic
  );
end entity;

architecture rtl of system is

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

  component self_test is
    port (
      mclk : in std_logic;
      reset : in std_logic;
      duty_cycle : out std_logic_vector(7 downto 0)
    );
  end component;

  component pulse_width_modulator is
    port(
      mclk : in std_logic; -- 100MHz
      reset : in std_logic; -- Asynchronous reset, active high
      duty_cycle : in std_logic_vector(7 downto 0); -- Signed
      dir : out std_logic;
      en : out std_logic
    );
  end component;

  component output_synchronizer is
    port (
      clk : in std_logic; --100MHz
      reset : in std_logic;
      dir : in std_logic;
      en : in std_logic;
      dir_synch : out std_logic;
      en_synch : out std_logic
    );
  end component;

  signal sa : std_logic;
  signal sb : std_logic;
  signal sa_synch : std_logic;
  signal sb_synch : std_logic;
  signal pos_inc : std_logic;
  signal pos_dec : std_logic;
  signal velocity : signed(7 downto 0);
  signal d0 : std_logic_vector(3 downto 0);
  signal d1 : std_logic_vector(3 downto 0);
  signal abcdefg : std_logic_vector(6 downto 0);
  signal c : std_logic;

  signal duty_cycle : std_logic_vector(7 downto 0);
  signal dir : std_logic;
  signal en : std_logic;
  signal dir_synch : std_logic;
  signal en_synch : std_logic;

begin

  IN_SYNCH_U : input_synchronizer
    port map (
      clk => clk,
      reset => reset,
      sa => sa,
      sb => sb,
      sa_synch => sa_synch,
      sb_synch => sb_synch
    );

  QUAD_DECODER_U : quadrature_decoder
    port map (
      clk => clk,
      reset => reset,
      sa => sa_synch,
      sb => sb_synch,
      pos_inc => pos_inc,
      pos_dec => pos_dec
    );
  
  VELOCITY_U : velocity_reader
    port map(
      mclk => clk,
      reset => reset, 
      pos_inc => pos_inc,
      pos_dec => pos_dec,
      velocity => velocity
    );

  SEG7CTRL_U : seg7ctrl
    port map(
      mclk => clk,
      reset => reset,
      d0 => d0,
      d1 => d1,
      abcdefg => abcdefg,
      c => c
    );

  SELF_TEST_U : self_test
    port map (
      mclk => clk,
      reset => reset,
      duty_cycle => duty_cycle
    );

  PWM_U : pulse_width_modulator
    port map (
      mclk => clk,
      reset => reset,
      duty_cycle => duty_cycle,
      dir => dir,
      en => en
    );

  OUTPUT_SYNCH_U : output_synchronizer
    port map (
      clk => clk,
      reset => reset,
      dir => dir,
      en => en,
      dir_synch => dir_synch,
      en_synch => en_synch
    );

  dir_out <= dir_synch;
  en_out <= en_synch;
  abcdefg_out <= abcdefg;
  c_out <= c;
  sa <= sa_in;
  sb <= sb_in;

  
  d0 <= std_logic_vector("abs"(velocity)(7 downto 4));
  d1 <= std_logic_vector("abs"(velocity)(3 downto 0));
  


end architecture;
      
