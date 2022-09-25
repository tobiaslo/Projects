Library IEEE;
use IEEE.std_logic_1164.all; 
use IEEE.numeric_std.all;

entity tb_pwm is
end entity;

architecture testbench of tb_pwm is

  constant HALF_PERIOD : time := 5 ns;

  signal tb_mclk : std_logic := '0';
  signal tb_reset : std_logic := '0';
  signal tb_duty_cycle : std_logic_vector(7 downto 0);
  signal tb_pwm_signal : std_logic;

  component pwm is
    port (
      clk : in std_logic;
      reset : in std_logic;
      duty_cycle : in std_logic_vector(7 downto 0) := (others => '0');
      pwm : out std_logic
    );
  end component;

begin


  PWM_MOD: pwm
    port map (
      clk => tb_mclk,
      reset => tb_reset,
      duty_cycle => tb_duty_cycle,
      pwm => tb_pwm_signal
    );

  tb_mclk <= not tb_mclk after HALF_PERIOD;

  tb_duty_cycle <= "00000100", "00111100" after 500 us;


end architecture;

 
