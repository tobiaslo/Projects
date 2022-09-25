Library IEEE;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

entity tb_output_synchronizer is
end entity;

architecture testbench of tb_output_synchronizer is

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

  component pulse_width_modulator is
    port(
      mclk      : in std_logic; 
      reset     : in std_logic; -- Asynchronous reset, active high
      duty_cycle: in std_logic_vector(7 downto 0);
      dir       : out std_logic;
      en        : out std_logic
    );
  end component;

  component self_test is
    generic (
      delay: unsigned
    );
    port (
      mclk : in std_logic;
      reset : in std_logic;
      duty_cycle : out std_logic_vector(7 downto 0)
    );
  end component;

  constant HALF_PERIOD : time := 5 ns;

  signal tb_clk : std_logic := '0';
  signal tb_reset : std_logic := '0';
  signal tb_duty_cycle : std_logic_vector(7 downto 0);
  signal tb_dir : std_logic;
  signal tb_en : std_logic;
  signal tb_dir_synch : std_logic;
  signal tb_en_synch : std_logic;

begin

  SELFTEST_U : self_test
    generic map (
      delay => d"2000"
    )
    port map (
      mclk => tb_clk,
      reset => tb_reset,
      duty_cycle => tb_duty_cycle
    );

  PWM_U : pulse_width_modulator
    port map (
      mclk => tb_clk,
      reset => tb_reset,
      duty_cycle => tb_duty_cycle,
      dir => tb_dir,
      en => tb_en
    );

  SYNC_U : output_synchronizer
    port map (
      clk => tb_clk,
      reset => tb_reset,
      dir => tb_dir,
      en => tb_en,
      dir_synch => tb_dir_synch,
      en_synch => tb_en_synch
    );

  tb_clk <= not tb_clk after HALF_PERIOD;


end architecture;
