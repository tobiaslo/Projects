Library IEEE;
use ieee.std_logic_1164.all;
use std.textio.all;
use ieee.numeric_std.all;

entity tb_selftest is
end entity;

architecture testbench of tb_selftest is

  component selftest is
    generic (
      delay: unsigned
    );
    port (
      mclk : in std_logic;
      reset : in std_logic;
      duty_cycle : out std_logic_vector(7 downto 0)
    );
  end component;
  

  signal tb_mclk : std_logic := '0';
  signal tb_reset : std_logic := '0';
  signal tb_duty_cycle : std_logic_vector(7 downto 0);

begin

  UUT: entity work.self_test(rtl)
    generic map (
      delay => d"100"
    )
    port map (
      mclk => tb_mclk,
      reset => tb_reset,
      duty_cycle => tb_duty_cycle
    );


  P_CLK: process
  begin
    tb_mclk <= '0';
    wait for 5 ns;
    tb_mclk <= '1';
    wait for 5 ns;
  end process P_CLK;


end architecture;


